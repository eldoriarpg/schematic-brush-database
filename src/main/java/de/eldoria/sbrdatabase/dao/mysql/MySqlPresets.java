/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.mysql;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.chojo.sqlutil.base.QueryFactoryHolder;
import de.eldoria.schematicbrush.storage.preset.Preset;
import de.eldoria.schematicbrush.storage.preset.PresetContainer;
import de.eldoria.schematicbrush.storage.preset.Presets;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.entity.Player;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MySqlPresets extends QueryFactoryHolder implements Presets {
    private MySqlPresetContainer global;
    private final Cache<UUID, MySqlPresetContainer> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .maximumSize(50)
            .build();

    public MySqlPresets(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public CompletableFuture<Optional<Preset>> getPreset(Player player, String name) {
        throw new NotImplementedException();
    }

    @Override
    public CompletableFuture<Optional<Preset>> getGlobalPreset(String name) {
        throw new NotImplementedException();
    }

    @Override
    public CompletableFuture<Void> addPreset(Player player, Preset preset) {
        throw new NotImplementedException();
    }

    @Override
    public CompletableFuture<Void> addPreset(Preset preset) {
        throw new NotImplementedException();
    }

    @Override
    public CompletableFuture<Boolean> removePreset(Player player, String name) {
        throw new NotImplementedException();
    }

    @Override
    public CompletableFuture<Boolean> removePreset(String name) {
        throw new NotImplementedException();
    }

    @Override
    public PresetContainer playerContainer(UUID player) {
        try {
            return cache.get(player, () -> new MySqlPresetContainer(player, this));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PresetContainer globalContainer() {
        if (global == null) {
            global = new MySqlPresetContainer(null, this);
        }
        return global;
    }

    @Override
    public CompletableFuture<Map<UUID, ? extends PresetContainer>> getPlayerPresets() {
        return null;
    }

    @Override
    public List<String> complete(Player player, String arg) {
        return null;
    }

    @Override
    public CompletableFuture<Integer> count() {
        return null;
    }
}
