/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package de.eldoria.sbrdatabase.dao.mysql;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.base.BaseContainer;
import de.eldoria.schematicbrush.storage.preset.Preset;
import de.eldoria.schematicbrush.storage.preset.PresetContainer;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static de.chojo.sadu.queries.api.call.Call.call;
import static de.chojo.sadu.queries.converter.StandardValueConverter.UUID_BYTES;

public class MySqlPresetContainer extends BaseContainer<Preset> implements PresetContainer {

    public MySqlPresetContainer(@Nullable UUID uuid, Configuration configuration, QueryConfiguration config, ObjectMapper mapper) {
        super(uuid, configuration, config, mapper);
    }

    @Override
    public CompletableFuture<Optional<Preset>> get(String name) {
        return CompletableFuture.supplyAsync(() ->
                query("SELECT preset FROM presets WHERE uuid = ? AND name LIKE ?")
                        .single(call().bind(owner(), UUID_BYTES).bind(name))
                        .map(resultSet -> parseToObject(resultSet.getString("preset"), Preset.class))
                        .first());
    }

    @Override
    public CompletableFuture<List<Preset>> page(int page, int size) {
        return CompletableFuture.supplyAsync(() ->
                query("SELECT preset FROM presets WHERE uuid = ? ORDER BY name LIMIT ? OFFSET ?")
                        .single(call().bind(owner(), UUID_BYTES).bind(size).bind(size * page))
                        .map(rs -> parseToObject(rs.getString("preset"), Preset.class))
                        .all());
    }

    @Override
    public CompletableFuture<Void> add(Preset preset) {
        return CompletableFuture.runAsync(() ->
                query("INSERT INTO presets(uuid, name, preset) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE preset = VALUES(preset)")
                        .single(call().bind(owner(), UUID_BYTES).bind(preset.name()).bind(parseToString(preset)))
                        .insert());
    }

    @Override
    public CompletableFuture<Collection<Preset>> all() {
        return CompletableFuture.supplyAsync(() ->
                query("SELECT uuid, name, preset FROM presets WHERE uuid = ?")
                .single(call().bind(owner(), UUID_BYTES))
                .map(resultSet -> parseToObject(resultSet.getString("preset"), Preset.class))
                .all());
    }

    @Override
    public CompletableFuture<Boolean> remove(String name) {
        return CompletableFuture.supplyAsync(() -> query("DELETE FROM presets WHERE uuid = ? AND name LIKE ?")
                .single(call().bind(owner(), UUID_BYTES).bind(name))
                .delete()
                .changed());
    }

    @Override
    protected CompletableFuture<List<String>> retrieveNames() {
        return CompletableFuture.supplyAsync(() ->query("SELECT name FROM presets WHERE uuid = ?")
                .single(call().bind(owner(), UUID_BYTES))
                .map(row -> row.getString("name"))
                .all());
    }

    @Override
    public CompletableFuture<Integer> size() {
        return CompletableFuture.supplyAsync( () ->query("SELECT count(1) as count FROM presets WHERE uuid = ?")
                .single(call().bind(owner(), UUID_BYTES))
                .map(rs -> rs.getInt("count"))
                .first().orElse(0));
    }
}
