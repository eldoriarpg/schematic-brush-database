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
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.mariadb.MariaDbPresets;
import de.eldoria.sbrdatabase.dao.mysql.MySqlPresets;
import de.eldoria.sbrdatabase.dao.postgres.PostgresPresets;
import de.eldoria.schematicbrush.SchematicBrushReborn;
import de.eldoria.schematicbrush.brush.config.util.Nameable;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class SbrDatabase extends EldoPlugin {
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

    private void registerStorageTypes() throws IOException, SQLException {
        var storages = configuration.storages();
        for (var sqlType : new SqlType<?>[]{SqlType.MYSQL, SqlType.POSTGRES, SqlType.MARIADB}) {
            if (!storages.isActive(sqlType)) continue;
            logger().info("Setting up storage for " + sqlType.getName()
            );
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
                .create())
                .build();
        SqlUpdater.builder(dataSource, SqlType.MARIADB)
                .withLogger(LoggerAdapter.wrap(logger()))
                .execute();
        var presets = new MariaDbPresets(dataSource);
        sbr.presetStorage().register(Nameable.of("mariadb"), presets);

    }

    private void setupMySql() throws IOException, SQLException {
        var storages = configuration.storages();
        var dataSource = applyHikariSettings(DataSourceCreator.create(SqlType.MYSQL)
                .configure(config -> applyBaseDb(storages.mysql(), config))
                .create())
                .build();
        SqlUpdater.builder(dataSource, SqlType.MYSQL)
                .withLogger(LoggerAdapter.wrap(logger()))
                .execute();
        var presets = new MySqlPresets(dataSource);
        sbr.presetStorage().register(Nameable.of("mariadb"), presets);
    }

    private void setupPostgres() throws IOException, SQLException {
        var storages = configuration.storages();
        var postgres = storages.postgres();
        var dataSource = applyHikariSettings(DataSourceCreator.create(SqlType.POSTGRES)
                .configure(config -> applyBaseDb(postgres, config)).create())
                .build();
        SqlUpdater.builder(dataSource, SqlType.POSTGRES)
                .withLogger(LoggerAdapter.wrap(logger()))
                .setReplacements(new QueryReplacement("sbr_database", postgres.schema()))
                .setSchemas(postgres.schema())
                .execute();
        dataSource.close();
        dataSource = applyHikariSettings(DataSourceCreator.create(SqlType.POSTGRES)
                .configure(config -> applyBaseDb(postgres, config))
                .create())
                .forSchema(postgres.schema())
                .build();
        var presets = new PostgresPresets(dataSource);
        sbr.presetStorage().register(Nameable.of("postgres"), presets);
    }

    private <T extends RemoteJdbcConfig<T>> void applyBaseDb(BaseDbConfig config, RemoteJdbcConfig<T> remote) {
        remote.host(config.host())
                .port(config.port())
                .database(config.database())
                .user(config.user())
                .password(config.password());
    }

    private ConfigurationStage applyHikariSettings(ConfigurationStage configurationStage) {
        //TODO: Config
        return configurationStage.withMinimumIdle(2).withMaximumPoolSize(5);
    }
}
