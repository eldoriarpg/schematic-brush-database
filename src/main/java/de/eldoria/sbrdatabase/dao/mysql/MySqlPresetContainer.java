/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.mysql;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.chojo.sadu.base.QueryFactory;
import de.chojo.sadu.wrapper.util.UpdateResult;
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

public class MySqlPresetContainer extends BaseContainer<Preset> implements PresetContainer {

    public MySqlPresetContainer(@Nullable UUID uuid, Configuration configuration, QueryFactory factoryHolder, ObjectMapper mapper) {
        super(uuid, configuration, factoryHolder, mapper);
    }

    @Override
    public CompletableFuture<Optional<Preset>> get(String name) {
        return builder(Preset.class).query("SELECT preset FROM presets WHERE uuid = ? AND name LIKE ?")
                .parameter(stmt -> stmt.setUuidAsBytes(owner())
                        .setString(name))
                .readRow(resultSet -> parseToObject(resultSet.getString("preset"), Preset.class))
                .first();
    }

    @Override
    public CompletableFuture<List<Preset>> page(int page, int size) {
        return builder(Preset.class).query("SELECT preset FROM presets WHERE uuid = ? ORDER BY name LIMIT ? OFFSET ?")
                .parameter(stmt -> stmt.setUuidAsBytes(owner())
                        .setInt(size)
                        .setInt(size * page))
                .readRow(rs -> parseToObject(rs.getString("preset"), Preset.class))
                .all();
    }

    @Override
    public CompletableFuture<Void> add(Preset preset) {
        return builder().query("INSERT INTO presets(uuid, name, preset) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE preset = ?")
                .parameter(stmt -> stmt.setUuidAsBytes(owner())
                        .setString(preset.name())
                        .setString(parseToString(preset))
                        .setString(parseToString(preset))).insert().execute().thenApply(r -> null);
    }

    @Override
    public CompletableFuture<Collection<Preset>> all() {
        return builder(Preset.class).query("SELECT uuid, name, preset FROM presets WHERE uuid = ?")
                .parameter(stmt -> stmt.setUuidAsBytes(owner()))
                .readRow(resultSet -> parseToObject(resultSet.getString("preset"), Preset.class))
                .all()
                .thenApply(list -> list);
    }

    @Override
    public CompletableFuture<Boolean> remove(String name) {
        return builder(Boolean.class).query("DELETE FROM presets WHERE uuid = ? AND name LIKE ?")
                .parameter(stmt -> stmt.setUuidAsBytes(owner())
                        .setString(name))
                .delete()
                .send()
                .thenApply(UpdateResult::changed);
    }

    @Override
    protected CompletableFuture<List<String>> retrieveNames() {
        return builder(String.class).query("SELECT name FROM presets WHERE uuid = ?")
                .parameter(stmt -> stmt.setUuidAsBytes(owner()))
                .readRow(resultSet -> resultSet.getString("name"))
                .all();
    }

    @Override
    public CompletableFuture<Integer> size() {
        return builder(Integer.class).query("SELECT count(1) as count FROM presets WHERE uuid = ?")
                .parameter(stmt -> stmt.setUuidAsBytes(owner()))
                .readRow(rs -> rs.getInt("count"))
                .first().thenApply(e -> e.orElse(0));
    }
}
