/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.base;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.chojo.sadu.queries.api.query.ParsedQuery;
import de.chojo.sadu.queries.configuration.ConnectedQueryConfigurationImpl;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.schematicbrush.storage.brush.BrushContainer;
import de.eldoria.schematicbrush.storage.brush.Brushes;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public abstract class BaseBrushes implements Brushes {
    private final Cache<UUID, BrushContainer> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .maximumSize(50)
            .build();
    private final QueryConfiguration queryConfiguration;
    private final Configuration configuration;
    private BrushContainer global;

    public BaseBrushes(QueryConfiguration queryConfiguration, Configuration configuration) {
        this.queryConfiguration = queryConfiguration;
        this.configuration = configuration;
    }

    @Override
    public BrushContainer playerContainer(UUID player) {
        try {
            return cache.get(player, () -> getContainer(player));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BrushContainer globalContainer() {
        if (global == null) {
            global = getContainer(null);
        }
        return global;
    }

    public abstract BrushContainer getContainer(UUID uuid);

    public Configuration configuration() {
        return configuration;
    }

    public ConnectedQueryConfigurationImpl withSingleTransaction() {
        return queryConfiguration.withSingleTransaction();
    }

    public ParsedQuery query(String sql, Object... format) {
        return queryConfiguration.query(sql, format);
    }

    public QueryConfiguration queryConfiguration() {
        return queryConfiguration;
    }
}
