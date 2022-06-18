/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.base;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.chojo.sqlutil.base.QueryFactoryHolder;
import de.eldoria.schematicbrush.storage.brush.BrushContainer;
import de.eldoria.schematicbrush.storage.brush.Brushes;

import javax.sql.DataSource;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public abstract class BaseBrushes extends QueryFactoryHolder implements Brushes {
    private final Cache<UUID, BrushContainer> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .maximumSize(50)
            .build();
    private BrushContainer global;

    public BaseBrushes(DataSource dataSource) {
        super(dataSource);
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
}
