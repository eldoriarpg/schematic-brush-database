/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase;

import de.chojo.sqlutil.databases.SqlType;
import de.chojo.sqlutil.datasource.DataSourceCreator;
import de.chojo.sqlutil.datasource.stage.ConfigurationStage;
import de.chojo.sqlutil.jdbc.RemoteJdbcConfig;
import de.chojo.sqlutil.logging.LoggerAdapter;
import de.chojo.sqlutil.updater.QueryReplacement;
import de.chojo.sqlutil.updater.SqlUpdater;
import de.chojo.sqlutil.wrapper.QueryBuilderConfig;
import de.eldoria.eldoutilities.plugin.EldoPlugin;
import de.eldoria.sbrdatabase.configuration.BaseDbConfig;
import de.eldoria.sbrdatabase.configuration.Cache;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.configuration.PostgresDbConfig;
import de.eldoria.sbrdatabase.configuration.Storages;
import de.eldoria.sbrdatabase.dao.mariadb.MariaDbStorage;
import de.eldoria.sbrdatabase.dao.mysql.MySqlStorage;
import de.eldoria.sbrdatabase.dao.postgres.PostgresStorage;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

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
    public void onPluginEnable() throws Throwable {
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
        for (var sqlType : new SqlType<?>[]{SqlType.MYSQL, SqlType.POSTGRES, SqlType.MARIADB}) {
            if (!storages.isActive(sqlType)) continue;
            logger().info("Setting up storage for " + sqlType.getName());
            switch (sqlType.getName()) {
                case "mariadb" -> setupMariaDb();
                case "postgres" -> setupPostgres();
                case "mysql" -> setupMySql();
            }
        }
    }

    private void setupMariaDb() throws IOException, SQLException {
        var storages = configuration.storages();
        var dataSource = applyHikariSettings(DataSourceCreator.create(SqlType.MARIADB)
                .configure(config -> applyBaseDb(storages.mariadb(), config))
                .create(), storages.mariadb())
                .withMaximumPoolSize(storages.mariadb().connections())
                .build();
        SqlUpdater.builder(dataSource, SqlType.MARIADB)
                .withLogger(LoggerAdapter.wrap(logger()))
                .setVersionTable("sbr_version")
                .execute();
        sbr.storageRegistry().register(mariadb, new MariaDbStorage(dataSource, configuration));
    }

    private void setupMySql() throws IOException, SQLException {
        var storages = configuration.storages();
        var dataSource = applyHikariSettings(DataSourceCreator.create(SqlType.MYSQL)
                .configure(config -> applyBaseDb(storages.mysql(), config))
                .create(), storages.mysql())
                .withMaximumPoolSize(storages.mysql().connections())
                .build();
        SqlUpdater.builder(dataSource, SqlType.MYSQL)
                .withLogger(LoggerAdapter.wrap(logger()))
                .setVersionTable("sbr_version")
                .execute();
        sbr.storageRegistry().register(mysql, new MySqlStorage(dataSource, configuration));
    }

    private void setupPostgres() throws IOException, SQLException {
        var storages = configuration.storages();
        var db = storages.postgres();
        var dataSource = applyHikariSettings(DataSourceCreator.create(SqlType.POSTGRES)
                .configure(config -> applyBaseDb(db, config)).create(), db)
                .build();
        SqlUpdater.builder(dataSource, SqlType.POSTGRES)
                .withLogger(LoggerAdapter.wrap(logger()))
                .setReplacements(new QueryReplacement("sbr_database", db.schema()))
                .setSchemas(db.schema())
                .setVersionTable("sbr_version")
                .execute();
        dataSource.close();
        dataSource = applyHikariSettings(DataSourceCreator.create(SqlType.POSTGRES)
                .configure(config -> applyBaseDb(db, config))
                .create(), db)
                .forSchema(db.schema())
                .build();
        sbr.storageRegistry().register(postgres, new PostgresStorage(dataSource, configuration));
    }

    private <T extends RemoteJdbcConfig<T>> void applyBaseDb(BaseDbConfig config, RemoteJdbcConfig<T> remote) {
        remote.host(config.host())
                .port(config.port())
                .database(config.database())
                .user(config.user())
                .password(config.password());
    }

    private ConfigurationStage applyHikariSettings(ConfigurationStage configurationStage, BaseDbConfig dbConfig) {
        return configurationStage.withMinimumIdle(1).withMaximumPoolSize(dbConfig.connections());
    }
}
