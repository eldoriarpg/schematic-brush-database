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
        var dataSource = applyBaseDb(SqlType.MARIADB, configuration.storages().mariadb()).build();
        updater(dataSource, SqlType.MARIADB).execute();
        sbr.storageRegistry().register(SbrDatabase.mariadb, new MariaDbStorage(dataSource, configuration));
    }

    private void setupMySql() throws IOException, SQLException {
        var dataSource = applyBaseDb(SqlType.MYSQL, configuration.storages().mysql()).build();
        updater(dataSource, SqlType.MYSQL).execute();
        sbr.storageRegistry().register(SbrDatabase.mysql, new MySqlStorage(dataSource, configuration));
    }

    private void setupPostgres() throws IOException, SQLException {
        var postgres = configuration.storages().postgres();
        var dataSource = applyBaseDb(SqlType.POSTGRES, postgres).build();
        updater(dataSource, SqlType.POSTGRES)
                .setReplacements(new QueryReplacement("sbr_database", postgres.schema()))
                .setSchemas(postgres.schema())
                .execute();
        dataSource.close();
        dataSource = applyBaseDb(SqlType.POSTGRES, postgres)
                .forSchema(postgres.schema())
                .build();
        sbr.storageRegistry().register(SbrDatabase.postgres, new PostgresStorage(dataSource, configuration));
    }

    private SqlUpdater.SqlUpdaterBuilder<?> updater(DataSource dataSource, SqlType<?> type) throws IOException {
        return SqlUpdater.builder(dataSource, type)
                .setVersionTable("sbr_version")
                .withLogger(LoggerAdapter.wrap(getLogger()));
    }

    private <T extends RemoteJdbcConfig<T>> ConfigurationStage applyBaseDb(SqlType<T> type, BaseDbConfig config) {
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
