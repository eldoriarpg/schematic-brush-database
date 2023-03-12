/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.mysql;

import de.chojo.sadu.base.QueryFactory;
import de.chojo.sadu.wrapper.util.UpdateResult;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.base.BaseContainer;
import de.eldoria.schematicbrush.storage.brush.Brush;
import de.eldoria.schematicbrush.storage.brush.BrushContainer;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySqlBrushContainer extends BaseContainer<Brush> implements BrushContainer {

    public MySqlBrushContainer(@Nullable UUID uuid, Configuration configuration, QueryFactory factoryHolder) {
        super(uuid, configuration, factoryHolder);
    }

    @Override
    public CompletableFuture<Optional<Brush>> get(String name) {
        return builder(Brush.class).query("SELECT brush FROM brushes WHERE uuid = ? AND name LIKE ?")
                .parameter(stmt -> stmt.setUuidAsBytes(owner())
                        .setString(name))
                .readRow(rs -> yamlToObject(rs.getString("brush"), Brush.class))
                .first();
    }

    public CompletableFuture<List<Brush>> page(int page, int size) {
        return builder(Brush.class).query("SELECT brush FROM brushes WHERE uuid = ? ORDER BY name LIMIT ? OFFSET ?")
                .parameter(stmt -> stmt.setUuidAsBytes(owner())
                        .setInt(size)
                        .setInt(size * page))
                .readRow(rs -> yamlToObject(rs.getString("brush"), Brush.class))
                .all();
    }

    @Override
    public CompletableFuture<Void> add(Brush preset) {
        return builder().query("INSERT INTO brushes(uuid, name, brush) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE brush = ?")
                .parameter(stmt -> stmt.setUuidAsBytes(owner())
                        .setString(preset.name())
                        .setString(presetToYaml(preset))
                        .setString(presetToYaml(preset)))
                .insert()
                .send()
                .thenApply(r -> null);
    }


    @Override
    public CompletableFuture<Collection<Brush>> all() {
        return builder(Brush.class).query("SELECT uuid, name, brush FROM brushes WHERE uuid = ?")
                .parameter(stmt -> stmt.setUuidAsBytes(owner()))
                .readRow(resultSet -> yamlToObject(resultSet.getString("preset"), Brush.class))
                .all()
                .thenApply(list -> list);
    }

    @Override
    public CompletableFuture<Boolean> remove(String name) {
        return builder(Boolean.class).query("DELETE FROM brushes WHERE uuid = ? AND name LIKE ?")
                .parameter(stmt -> stmt.setUuidAsBytes(owner())
                        .setString(name))
                .delete()
                .send()
                .thenApply(UpdateResult::changed);
    }

    @Override
    protected CompletableFuture<List<String>> retrieveNames() {
        return builder(String.class).query("SELECT name FROM brushes WHERE uuid = ?")
                .parameter(stmt -> stmt.setUuidAsBytes(owner()))
                .readRow(resultSet -> resultSet.getString("name"))
                .all();
    }

    @Override
    public CompletableFuture<Integer> size() {
        return builder(Integer.class).query("SELECT count(1) FROM brushes WHERE uuid = ?")
                .parameter(stmt -> stmt.setUuidAsBytes(owner()))
                .readRow(rs -> rs.getInt("count"))
                .first().thenApply(e -> e.orElse(0));
    }
}
