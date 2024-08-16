/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.zaxxer.hikari.HikariDataSource;
import de.chojo.sadu.core.databases.Database;
import de.chojo.sadu.core.jdbc.RemoteJdbcConfig;
import de.chojo.sadu.core.updater.SqlVersion;
import de.chojo.sadu.core.updater.UpdaterBuilder;
import de.chojo.sadu.datasource.DataSourceCreator;
import de.chojo.sadu.datasource.stage.ConfigurationStage;
import de.chojo.sadu.mariadb.databases.MariaDb;
import de.chojo.sadu.mysql.databases.MySql;
import de.chojo.sadu.postgresql.databases.PostgreSql;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.chojo.sadu.updater.QueryReplacement;
import de.chojo.sadu.updater.SqlUpdater;
import de.eldoria.eldoutilities.config.template.PluginBaseConfiguration;
import de.eldoria.eldoutilities.plugin.EldoPlugin;
import de.eldoria.eldoutilities.updater.lynaupdater.LynaUpdateChecker;
import de.eldoria.eldoutilities.updater.lynaupdater.LynaUpdateData;
import de.eldoria.sbrdatabase.configuration.JacksonConfiguration;
import de.eldoria.sbrdatabase.configuration.LegacyConfiguration;
import de.eldoria.sbrdatabase.configuration.elements.Cache;
import de.eldoria.sbrdatabase.configuration.elements.Storages;
import de.eldoria.sbrdatabase.configuration.elements.storages.BaseDbConfig;
import de.eldoria.sbrdatabase.configuration.elements.storages.PostgresDbConfig;
import de.eldoria.sbrdatabase.dao.base.BaseContainer;
import de.eldoria.sbrdatabase.dao.mariadb.MariaDbStorage;
import de.eldoria.sbrdatabase.dao.mysql.MySqlStorage;
import de.eldoria.sbrdatabase.dao.postgres.PostgresStorage;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import de.eldoria.schematicbrush.storage.StorageRegistry;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;

public class SbrDatabase extends EldoPlugin {
    private static final Nameable mariadb = Nameable.of("mariadb");
    private static final Nameable mysql = Nameable.of("mysql");
    private static final Nameable postgres = Nameable.of("postgres");
    public static final Nameable[] sqlTypes = {mariadb, mysql, postgres};
    private ObjectMapper mapper;
    private final List<HikariDataSource> sources = new ArrayList<>();
    private JacksonConfiguration configuration;
    private SchematicBrushReborn sbr;

    public SbrDatabase() {
        configuration = new JacksonConfiguration(this);
    }

    public SbrDatabase(@NotNull JavaPluginLoader loader, @NotNull PluginDescriptionFile description, @NotNull File dataFolder, @NotNull File file) {
        super(loader, description, dataFolder, file);
        configuration = new JacksonConfiguration(this);
    }

    @Override
    public void onPluginLoad() throws Throwable {
        sbr = SchematicBrushReborn.instance();
        var builder = JsonMapper.builder();
        mapper = sbr.configureMapper(builder);
        QueryConfiguration.setDefault(QueryConfiguration.builder(null)
                .setExceptionHandler(ex -> getLogger().log(Level.SEVERE, "SQL Exception occurred.", ex))
                .build());

        PluginBaseConfiguration base = configuration.secondary(PluginBaseConfiguration.KEY);
        if (base.version() == 0) {
            var legacyConfiguration = new LegacyConfiguration(this);
            getLogger().log(Level.INFO, "Migrating configuration to jackson.");
            configuration.main().cache(legacyConfiguration.cache());
            configuration.main().storages(legacyConfiguration.storages());
            base.version(1);
            base.lastInstalledVersion(this);
            configuration.save();
        }

        if (configuration.main().checkUpdates()) {
            LynaUpdateChecker.lyna(LynaUpdateData.builder(this, 9).build()).start();
        }

        registerStorageTypes();
        configuration.save();
    }

    @Override
    public void onPluginDisable() throws Throwable {
        configuration.save();
        for (HikariDataSource source : sources) {
            source.close();
        }
    }

    @Override
    public Level getLogLevel() {
        return configuration.secondary(PluginBaseConfiguration.KEY).logLevel();
    }

    @Override
    public List<Class<? extends ConfigurationSerializable>> getConfigSerialization() {
        return List.of(Storages.class, BaseDbConfig.class, PostgresDbConfig.class, Cache.class);
    }

    private void registerStorageTypes() throws IOException, SQLException {
        var storages = configuration.storages();
        boolean active = false;
        for (var sqlType : sqlTypes) {
            if (!storages.isActive(sqlType)) continue;
            getLogger().info("Setting up storage for " + sqlType);
            switch (sqlType.name()) {
                case "mariadb" -> setupMariaDb();
                case "postgres" -> setupPostgres();
                case "mysql" -> setupMySql();
            }
            active = true;
        }
        if (!active) {
            getLogger().warning("No storage type active. Please enable a storage type.");
        }
    }

    private void setupMariaDb() throws IOException, SQLException {
        var source = applyBaseDb(MariaDb.get(), configuration.storages().mariadb()).build();
        sbr.storageRegistry().register(SbrDatabase.mariadb, new MariaDbStorage(QueryConfiguration.getDefault().edit(source).build(), configuration, mapper));
        SqlUpdater.builder(source, MariaDb.get())
                .withClassLoader(getClassLoader())
                .setVersionTable("sbr_version")
                .postUpdateHook(new SqlVersion(1, 1), version_1_1_migration(SbrDatabase.mariadb))
                .execute();
        sources.add(source);
    }

    private void setupMySql() throws IOException, SQLException {
        var source = applyBaseDb(MySql.get(), configuration.storages().mysql()).build();
        sbr.storageRegistry().register(SbrDatabase.mysql, new MySqlStorage(QueryConfiguration.getDefault().edit(source).build(), configuration, mapper));
        SqlUpdater.builder(source, MySql.get())
                .withClassLoader(getClassLoader())
                .setVersionTable("sbr_version")
                .postUpdateHook(new SqlVersion(1, 1), version_1_1_migration(SbrDatabase.mysql))
                .execute();
        sources.add(source);
    }

    private void setupPostgres() throws IOException, SQLException {
        var postgres = configuration.storages().postgres();
        var source = applyBaseDb(PostgreSql.get(), postgres)
                .forSchema(postgres.schema())
                .build();

        var update = applyBaseDb(PostgreSql.get(), postgres).build();
        sbr.storageRegistry().register(SbrDatabase.postgres, new PostgresStorage(QueryConfiguration.getDefault().edit(source).build(), configuration, mapper));
        SqlUpdater.builder(update, PostgreSql.get())
                .withClassLoader(getClassLoader())
                .setReplacements(new QueryReplacement("sbr_database", postgres.schema()))
                .setSchemas(postgres.schema())
                .setVersionTable("sbr_version")
                .postUpdateHook(new SqlVersion(1, 1), version_1_1_migration(SbrDatabase.postgres))
                .execute();
        update.close();
        sources.add(source);
    }

    @SuppressWarnings("AssignmentToStaticFieldFromInstanceMethod")
    private Consumer<java.sql.Connection> version_1_1_migration(Nameable current) {
        return conn -> {
            BaseContainer.legacySerialization = true;
            sbr.storageRegistry().migrate(current, StorageRegistry.YAML).join();
            BaseContainer.legacySerialization = false;
            sbr.storageRegistry().migrate(StorageRegistry.YAML, current).join();
        };
    }

    private <T extends RemoteJdbcConfig<?>, U extends UpdaterBuilder<T, ?>> ConfigurationStage applyBaseDb(Database<T, U> type, BaseDbConfig config) {
        return DataSourceCreator.create(type)
                .configure(remote -> remote.host(config.host())
                        .port(config.port())
                        .database(config.database())
                        .user(config.user())
                        .password(config.password()))
                .create()
                .withPoolName("SBR Database %s".formatted(type.name()))
                .withMinimumIdle(1)
                .withMaximumPoolSize(config.connections());
    }
}
