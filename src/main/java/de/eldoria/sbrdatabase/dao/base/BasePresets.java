/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.base;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.chojo.sqlutil.base.QueryFactoryHolder;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.schematicbrush.storage.preset.PresetContainer;
import de.eldoria.schematicbrush.storage.preset.Presets;

import javax.sql.DataSource;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public abstract class BasePresets extends QueryFactoryHolder implements Presets {
    private final Cache<UUID, PresetContainer> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .maximumSize(50)
            .build();
    private PresetContainer global;
    private final Configuration configuration;

    public BasePresets(DataSource dataSource, Configuration configuration) {
        super(dataSource);
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
}
