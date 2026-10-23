/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package de.eldoria.sbrdatabase;

import com.zaxxer.hikari.HikariDataSource;
import de.chojo.sadu.core.databases.Database;
import de.chojo.sadu.core.exceptions.ThrowingConsumer;
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
import de.eldoria.sbrdatabase.configuration.JacksonConfiguration;
import de.eldoria.sbrdatabase.configuration.elements.storages.BaseDbConfig;
import de.eldoria.sbrdatabase.dao.mariadb.MariaDbStorage;
import de.eldoria.sbrdatabase.dao.mysql.MySqlStorage;
import de.eldoria.sbrdatabase.dao.postgres.PostgresStorage;
import de.eldoria.sbrdatabase.updates.Updates;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class SbrDatabase extends EldoPlugin {
    private static final Nameable mariadb = Nameable.of("mariadb");
    private static final Nameable mysql = Nameable.of("mysql");
    private static final Nameable postgres = Nameable.of("postgres");
    public static final Nameable[] sqlTypes = {mariadb, mysql, postgres};
    private ObjectMapper mapper;
    private final JacksonConfiguration configuration;
    private SchematicBrushReborn sbr;
    private Updates updates;

    public SbrDatabase() {
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
            base.version(1);
            base.lastInstalledVersion(this);
            configuration.save();
        }

        registerStorageTypes();
        configuration.save();
    }

    @Override
    public void onPluginEnable() throws Throwable {
        updates = new Updates(this, configuration);
        updates.apply();
    }

    @Override
    public void onPluginDisable() throws Throwable {
        if (updates != null) updates.stop();
        configuration.save();
    }

    @Override
    public Level getLogLevel() {
        return configuration.secondary(PluginBaseConfiguration.KEY).logLevel();
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
        closeOnFailure(source, () -> SqlUpdater.builder(source, MariaDb.get())
                .withClassLoader(getClassLoader())
                .setVersionTable("sbr_version")
                .preUpdateHook(new SqlVersion(1, 1), rejectYamlEntries(""))
                .execute());
        sbr.storageRegistry().register(SbrDatabase.mariadb, new MariaDbStorage(QueryConfiguration.getDefault().edit(source).build(), configuration, mapper));
    }

    private void setupMySql() throws IOException, SQLException {
        var source = applyBaseDb(MySql.get(), configuration.storages().mysql()).build();
        closeOnFailure(source, () -> SqlUpdater.builder(source, MySql.get())
                .withClassLoader(getClassLoader())
                .setVersionTable("sbr_version")
                .preUpdateHook(new SqlVersion(1, 1), rejectYamlEntries(""))
                .execute());
        sbr.storageRegistry().register(SbrDatabase.mysql, new MySqlStorage(QueryConfiguration.getDefault().edit(source).build(), configuration, mapper));
    }

    private void setupPostgres() throws IOException, SQLException {
        var postgres = configuration.storages().postgres();
        var source = applyBaseDb(PostgreSql.get(), postgres)
                .forSchema(postgres.schema())
                .build();

        try (var update = applyBaseDb(PostgreSql.get(), postgres).build()) {
            closeOnFailure(source, () -> SqlUpdater.builder(update, PostgreSql.get())
                    .withClassLoader(getClassLoader())
                    .setReplacements(new QueryReplacement("sbr_database", postgres.schema()))
                    .setSchemas(postgres.schema())
                    .setVersionTable("sbr_version")
                    .preUpdateHook(new SqlVersion(1, 1), rejectYamlEntries(postgres.schema() + "."))
                    .execute());
        }
        sbr.storageRegistry().register(SbrDatabase.postgres, new PostgresStorage(QueryConfiguration.getDefault().edit(source).build(), configuration, mapper));
    }

    /**
     * Runs the schema update for a new connection pool and closes the pool if the update fails. A pool that passes is
     * handed to a storage, and Schematic Brush Reborn closes it when it shuts its storages down.
     *
     * @param source pool the storage will use
     * @param update schema update to run
     * @throws IOException  if the update scripts cannot be read
     * @throws SQLException if the update fails
     */
    private void closeOnFailure(HikariDataSource source, SchemaUpdate update) throws IOException, SQLException {
        try {
            update.run();
        } catch (IOException | SQLException | RuntimeException e) {
            source.close();
            throw e;
        }
    }

    /**
     * Schema update of one database.
     */
    @FunctionalInterface
    private interface SchemaUpdate {
        /**
         * Brings the schema up to date.
         *
         * @throws IOException  if the update scripts cannot be read
         * @throws SQLException if the update fails
         */
        void run() throws IOException, SQLException;
    }

    /**
     * Stops the update of a database that still stores brushes and presets in the YAML format written before version
     * 1.1. Converting them requires the serialization of Schematic Brush Reborn 2, so such a database has to be updated
     * with a 1.x release of this plugin first. A freshly created database passes, as it holds no entries yet.
     *
     * @param tablePrefix prefix that qualifies the table names, e.g. a schema followed by a dot
     * @return hook to run before the update to version 1.1
     */
    private ThrowingConsumer<Connection, SQLException> rejectYamlEntries(String tablePrefix) {
        return conn -> {
            var query = "SELECT (SELECT count(1) FROM %1$sbrushes) + (SELECT count(1) FROM %1$spresets) AS count".formatted(tablePrefix);
            try (var stmt = conn.prepareStatement(query); var rs = stmt.executeQuery()) {
                if (rs.next() && rs.getLong("count") > 0) {
                    throw new SQLException("The database contains brushes or presets stored before version 1.1. Update it with SchematicBrushDatabase 1.1.10 before using version 2.");
                }
            }
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
