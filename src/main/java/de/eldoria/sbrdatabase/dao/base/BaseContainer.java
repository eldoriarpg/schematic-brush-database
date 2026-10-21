/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package de.eldoria.sbrdatabase.dao.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.chojo.sadu.queries.api.query.ParsedQuery;
import de.chojo.sadu.queries.configuration.ConnectedQueryConfigurationImpl;
import de.eldoria.eldoutilities.serialization.wrapper.YamlContainer;
import de.eldoria.eldoutilities.utils.Futures;
import de.eldoria.sbrdatabase.SbrDatabase;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.schematicbrush.storage.ContainerPagedAccess;
import de.eldoria.schematicbrush.storage.base.Container;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public static boolean legacySerialization = false;
    private final QueryConfiguration queryConfiguration;
    private final ObjectMapper mapper;

    public BaseContainer(@Nullable UUID uuid, Configuration configuration, QueryConfiguration queryConfiguration, ObjectMapper mapper) {
        this.uuid = uuid == null ? Container.GLOBAL : uuid;
        this.configuration = configuration;
        this.queryConfiguration = queryConfiguration;
        this.mapper = mapper;
    }

    protected <V extends ConfigurationSerializable> V parseToObject(String object, Class<V> clazz) throws SQLException {
        try {
            if (legacySerialization) return YamlContainer.yamlToObject(object, clazz);
            return mapper.readValue(object, clazz);
        } catch (InvalidConfigurationException | JsonProcessingException e) {
            throw new SQLException("Could not deserialize object", e);
        }
    }

    protected <V extends ConfigurationSerializable> String parseToString(V object) {
        try {
            if (legacySerialization) return YamlContainer.objectToYaml(object);
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Could not serialize object", e);
        }
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
