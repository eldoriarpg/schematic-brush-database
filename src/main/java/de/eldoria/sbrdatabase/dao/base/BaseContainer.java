/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.base;

import de.chojo.sqlutil.base.QueryFactoryHolder;
import de.chojo.sqlutil.conversion.UUIDConverter;
import de.eldoria.eldoutilities.serialization.wrapper.YamlContainer;
import de.eldoria.eldoutilities.utils.Futures;
import de.eldoria.sbrdatabase.SbrDatabase;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.schematicbrush.storage.ContainerPagedAccess;
import de.eldoria.schematicbrush.storage.base.Container;
import de.eldoria.schematicbrush.storage.brush.Brush;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public abstract class BaseContainer<T> extends QueryFactoryHolder implements Container<T> {
    private static final UUID GLOBAL = new UUID(0, 0);
    private final UUID uuid;
    private final Configuration configuration;
    private Set<String> names = Collections.emptySet();
    private Instant lastRefresh = Instant.MIN;

    public BaseContainer(@Nullable UUID uuid, Configuration configuration, QueryFactoryHolder factoryHolder) {
        super(factoryHolder);
        this.uuid = uuid == null ? GLOBAL : uuid;
        this.configuration = configuration;
    }

    protected <T extends ConfigurationSerializable> T yamlToObject(String preset, Class<T> clazz) {
        try {
            return YamlContainer.yamlToObject(preset, clazz);
        } catch (InvalidConfigurationException e) {
            SbrDatabase.logger().log(Level.SEVERE, "Could not deserialize preset", e);
            return null;
        }
    }

    protected <T extends ConfigurationSerializable> String presetToYaml(T preset) {
        return YamlContainer.objectToYaml(preset);
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

    public UUID uuid() {
        return uuid;
    }

    public byte[] uuidBytes() {
        return UUIDConverter.convert(uuid);
    }

    public abstract CompletableFuture<List<T>> page(int page, int size);

    @Override
    public CompletableFuture<ContainerPagedAccess<T>> paged() {
        return size().thenApply(size -> new DbContainerPagedAccess<>(this, size));
    }
}
