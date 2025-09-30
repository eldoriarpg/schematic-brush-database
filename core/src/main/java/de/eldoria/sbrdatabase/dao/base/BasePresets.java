/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package de.eldoria.sbrdatabase.dao.base;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.chojo.sadu.queries.api.query.ParsedQuery;
import de.chojo.sadu.queries.configuration.ConnectedQueryConfigurationImpl;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.schematicbrush.storage.preset.PresetContainer;
import de.eldoria.schematicbrush.storage.preset.Presets;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public abstract class BasePresets implements Presets {
    private final Cache<UUID, PresetContainer> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .maximumSize(50)
            .build();
    private final QueryConfiguration queryConfiguration;
    private final Configuration configuration;
    private PresetContainer global;

    public BasePresets(QueryConfiguration queryConfiguration, Configuration configuration) {
        this.queryConfiguration = queryConfiguration;
        this.configuration = configuration;
    }

    @Override
    public PresetContainer playerContainer(UUID player) {
        try {
            return cache.get(player, () -> getContainer(player));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PresetContainer globalContainer() {
        if (global == null) {
            global = getContainer(null);
        }
        return global;
    }

    public abstract PresetContainer getContainer(UUID uuid);

    public Configuration configuration() {
        return configuration;
    }

    public QueryConfiguration queryConfiguration() {
        return queryConfiguration;
    }

    public ParsedQuery query(String sql, Object... format) {
        return queryConfiguration.query(sql, format);
    }

    public ConnectedQueryConfigurationImpl withSingleTransaction() {
        return queryConfiguration.withSingleTransaction();
    }
}
