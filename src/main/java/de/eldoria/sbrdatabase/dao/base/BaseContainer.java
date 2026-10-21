/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package de.eldoria.sbrdatabase.dao.base;

import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.chojo.sadu.queries.api.query.ParsedQuery;
import de.chojo.sadu.queries.configuration.ConnectedQueryConfigurationImpl;
import de.eldoria.eldoutilities.utils.Futures;
import de.eldoria.sbrdatabase.SbrDatabase;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.schematicbrush.storage.ContainerPagedAccess;
import de.eldoria.schematicbrush.storage.base.Container;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public abstract class BaseContainer<T> implements Container<T> {
    @NotNull
    private final UUID uuid;
    private final Configuration configuration;
    private Set<String> names = Collections.emptySet();
    private Instant lastRefresh = Instant.MIN;
    private final QueryConfiguration queryConfiguration;
    private final ObjectMapper mapper;

    public BaseContainer(@Nullable UUID uuid, Configuration configuration, QueryConfiguration queryConfiguration, ObjectMapper mapper) {
        this.uuid = uuid == null ? Container.GLOBAL : uuid;
        this.configuration = configuration;
        this.queryConfiguration = queryConfiguration;
        this.mapper = mapper;
    }

    protected <V> V parseToObject(String object, Class<V> clazz) throws SQLException {
        try {
            return mapper.readValue(object, clazz);
        } catch (JacksonException e) {
            throw new SQLException("Could not deserialize object", e);
        }
    }

    protected <V> String parseToString(V object) {
        return mapper.writeValueAsString(object);
    }

    public final Set<String> names() {
        if (lastRefresh.isBefore(Instant.now().minus(configuration.cache().cacheRefreshSec(), ChronoUnit.SECONDS))) {
            lastRefresh = Instant.now();
            retrieveNames()
                    .whenComplete(Futures.whenComplete(
                            names -> this.names = new HashSet<>(names),
                            err -> SbrDatabase.logger().log(Level.SEVERE, "Could not refresh names", err)));
        }
        return names;
    }

    protected abstract CompletableFuture<List<String>> retrieveNames();

    public abstract CompletableFuture<List<T>> page(int page, int size);

    @Override
    public CompletableFuture<? extends ContainerPagedAccess<T>> paged() {
        return size().thenApply(size -> new DbContainerPagedAccess<>(this, size))
                .exceptionally(err -> {
                    SbrDatabase.logger().log(Level.SEVERE, "Could not build paged access", err);
                    return new DbContainerPagedAccess<>(this, 0);
                });
    }

    @Override
    public @NotNull UUID owner() {
        return uuid;
    }

    public ConnectedQueryConfigurationImpl withSingleTransaction() {
        return queryConfiguration.withSingleTransaction();
    }

    public ParsedQuery query(String sql, Object... format) {
        return queryConfiguration.query(sql, format);
    }
}
