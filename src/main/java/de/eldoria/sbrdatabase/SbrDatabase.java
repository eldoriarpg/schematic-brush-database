/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase;

import de.chojo.sadu.databases.Database;
import de.chojo.sadu.databases.MariaDb;
import de.chojo.sadu.databases.MySql;
import de.chojo.sadu.databases.PostgreSql;
import de.chojo.sadu.datasource.DataSourceCreator;
import de.chojo.sadu.datasource.stage.ConfigurationStage;
import de.chojo.sadu.jdbc.RemoteJdbcConfig;
import de.chojo.sadu.updater.QueryReplacement;
import de.chojo.sadu.updater.SqlUpdater;
import de.chojo.sadu.wrapper.QueryBuilderConfig;
import de.eldoria.eldoutilities.plugin.EldoPlugin;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.configuration.elements.Cache;
import de.eldoria.sbrdatabase.configuration.elements.Storages;
import de.eldoria.sbrdatabase.configuration.elements.storages.BaseDbConfig;
import de.eldoria.sbrdatabase.configuration.elements.storages.PostgresDbConfig;
import de.eldoria.sbrdatabase.dao.mariadb.MariaDbStorage;
import de.eldoria.sbrdatabase.dao.mysql.MySqlStorage;
import de.eldoria.sbrdatabase.dao.postgres.PostgresStorage;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class SbrDatabase extends EldoPlugin {
    private static final Nameable mariadb = Nameable.of("mariadb");
    private static final Nameable mysql = Nameable.of("mysql");
    private static final Nameable postgres = Nameable.of("postgres");
    public static final Nameable[] sqlTypes = {mariadb, mysql, postgres};
    private final Thread.UncaughtExceptionHandler exceptionHandler = (thread, err) -> logger().log(Level.SEVERE, "Unhandled exception occured in thread " + thread.getName() + "-" + thread.getId(), err);

    private final ExecutorService executor = Executors.newCachedThreadPool(run -> {
        var thread = new Thread(run, "DbThreads");
        thread.setUncaughtExceptionHandler(exceptionHandler);
        return thread;
    });
    private Configuration configuration;
    private SchematicBrushReborn sbr;

    @Override
    public void onPluginLoad() throws Throwable {
        sbr = SchematicBrushReborn.instance();
        QueryBuilderConfig.setDefault(QueryBuilderConfig.builder()
                .withExceptionHandler(ex -> logger().log(Level.SEVERE, "SQL Exception occured.", ex))
                .build());

        configuration = new Configuration(this);

        registerStorageTypes();
    }

    @Override
    public void onPostStart() throws Throwable {

    }

    @Override
    public void onPluginDisable() throws Throwable {
        executor.shutdown();
    }

    @Override
    public List<Class<? extends ConfigurationSerializable>> getConfigSerialization() {
        return List.of(Storages.class, BaseDbConfig.class, PostgresDbConfig.class, Cache.class);
    }

    private void registerStorageTypes() throws IOException, SQLException {
        var storages = configuration.storages();
        for (var sqlType : sqlTypes) {
            if (!storages.isActive(sqlType)) continue;
            getLogger().info("Setting up storage for " + sqlType);
            switch (sqlType.name()) {
                case "mariadb" -> setupMariaDb();
                case "postgres" -> setupPostgres();
                case "mysql" -> setupMySql();
            }
        }
    }

    private void setupMariaDb() throws IOException, SQLException {
        var dataSource = applyBaseDb(MariaDb.get(), configuration.storages().mariadb()).build();
        updater(dataSource, MariaDb.get()).execute();
        sbr.storageRegistry().register(SbrDatabase.mariadb, new MariaDbStorage(dataSource, configuration));
    }

    private void setupMySql() throws IOException, SQLException {
        var dataSource = applyBaseDb(MySql.get(), configuration.storages().mysql()).build();
        updater(dataSource, MySql.get()).execute();
        sbr.storageRegistry().register(SbrDatabase.mysql, new MySqlStorage(dataSource, configuration));
    }

    private void setupPostgres() throws IOException, SQLException {
        var postgres = configuration.storages().postgres();
        var dataSource = applyBaseDb(PostgreSql.get(), postgres).build();
        updater(dataSource, PostgreSql.get())
                .setReplacements(new QueryReplacement("sbr_database", postgres.schema()))
                .setSchemas(postgres.schema())
                .execute();
        dataSource.close();
        dataSource = applyBaseDb(PostgreSql.get(), postgres)
                .forSchema(postgres.schema())
                .build();
        sbr.storageRegistry().register(SbrDatabase.postgres, new PostgresStorage(dataSource, configuration));
    }

    private SqlUpdater.SqlUpdaterBuilder<?> updater(DataSource dataSource, Database<?> type) throws IOException {
        return SqlUpdater.builder(dataSource, type)
                .setVersionTable("sbr_version");
    }

    private <T extends RemoteJdbcConfig<?>> ConfigurationStage applyBaseDb(Database<T> type, BaseDbConfig config) {
        return DataSourceCreator.create(type)
                .configure(remote -> remote.host(config.host())
                        .port(config.port())
                        .database(config.database())
                        .user(config.user())
                        .password(config.password()))
                .create()
                .withMinimumIdle(1)
                .withMaximumPoolSize(config.connections());
    }
}
