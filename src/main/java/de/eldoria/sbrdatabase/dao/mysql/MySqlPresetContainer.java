/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.mysql;

import de.chojo.sqlutil.base.QueryFactoryHolder;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.base.BaseContainer;
import de.eldoria.schematicbrush.storage.preset.Preset;
import de.eldoria.schematicbrush.storage.preset.PresetContainer;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySqlPresetContainer extends BaseContainer<Preset> implements PresetContainer {

    public MySqlPresetContainer(@Nullable UUID uuid, Configuration configuration, QueryFactoryHolder factoryHolder) {
        super(uuid, configuration, factoryHolder);
    }

    @Override
    public CompletableFuture<Optional<Preset>> get(String name) {
        return builder(Preset.class).query("SELECT preset FROM presets WHERE uuid = ? AND name LIKE ?")
                .paramsBuilder(stmt -> stmt.setBytes(uuidBytes())
                        .setString(name))
                .readRow(resultSet -> yamlToObject(resultSet.getString("preset"), Preset.class))
                .first();
    }

    @Override
    public CompletableFuture<List<Preset>> page(int page, int size) {
        return builder(Preset.class).query("SELECT preset FROM presets WHERE uuid = ? ORDER BY name LIMIT ? OFFSET ?")
                .paramsBuilder(stmt -> stmt.setBytes(uuidBytes())
                        .setInt(size)
                        .setInt(size * page))
                .readRow(rs -> yamlToObject(rs.getString("preset"), Preset.class))
                .all();
    }

    @Override
    public CompletableFuture<Void> add(Preset preset) {
        return builder().query("INSERT INTO presets(uuid, name, preset) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE preset = ?")
                .paramsBuilder(stmt -> stmt.setBytes(uuidBytes())
                        .setString(preset.name())
                        .setString(presetToYaml(preset))
                        .setString(presetToYaml(preset))).insert().execute().thenApply(r -> null);
    }

    @Override
    public CompletableFuture<Collection<Preset>> all() {
        return builder(Preset.class).query("SELECT uuid, name, preset FROM presets WHERE uuid = ?")
                .paramsBuilder(stmt -> stmt.setBytes(uuidBytes()))
                .readRow(resultSet -> yamlToObject(resultSet.getString("preset"), Preset.class))
                .all()
                .thenApply(list -> list);
    }

    @Override
    public CompletableFuture<Boolean> remove(String name) {
        return builder(Boolean.class).query("DELETE FROM presets WHERE uuid = ? AND name LIKE ?")
                .paramsBuilder(stmt -> stmt.setBytes(uuidBytes())
                        .setString(name))
                .delete()
                .execute()
                .thenApply(i -> i == 1);
    }

    @Override
    protected CompletableFuture<List<String>> retrieveNames() {
        return builder(String.class).query("SELECT name FROM presets WHERE uuid = ?")
                .paramsBuilder(stmt -> stmt.setBytes(uuidBytes()))
                .readRow(resultSet -> resultSet.getString("name"))
                .all();
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public CompletableFuture<Integer> size() {
        return builder(Integer.class).query("SELECT COUNT(1) FROM presets WHERE uuid = ?")
                .paramsBuilder(stmt -> stmt.setBytes(uuidBytes()))
                .readRow(rs -> rs.getInt("count"))
                .first().thenApply(e -> e.orElse(0));
    }
}
