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
import de.eldoria.schematicbrush.storage.brush.Brush;
import de.eldoria.schematicbrush.storage.brush.BrushContainer;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static de.chojo.sadu.queries.api.call.Call.call;
import static de.chojo.sadu.queries.converter.StandardValueConverter.UUID_BYTES;

public class MySqlBrushContainer extends BaseContainer<Brush> implements BrushContainer {

    public MySqlBrushContainer(@Nullable UUID uuid, Configuration configuration, QueryConfiguration config, ObjectMapper mapper) {
        super(uuid, configuration, config, mapper);
    }

    @Override
    public CompletableFuture<Optional<Brush>> get(String name) {
        return CompletableFuture.supplyAsync(() ->
                query("SELECT brush FROM brushes WHERE uuid = ? AND name LIKE ?")
                        .single(call().bind(owner(), UUID_BYTES).bind(name))
                        .map(rs -> parseToObject(rs.getString("brush"), Brush.class))
                        .first());
    }

    public CompletableFuture<List<Brush>> page(int page, int size) {
        return CompletableFuture.supplyAsync(() ->
                query("SELECT brush FROM brushes WHERE uuid = ? ORDER BY name LIMIT ? OFFSET ?")
                        .single(call().bind(owner(), UUID_BYTES).bind(size).bind(size * page))
                        .map(rs -> parseToObject(rs.getString("brush"), Brush.class))
                        .all());
    }

    @Override
    public CompletableFuture<Void> add(Brush preset) {
        return CompletableFuture.runAsync(() ->
                query("INSERT INTO brushes(uuid, name, brush) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE brush = VALUES(preset)")
                        .single(call().bind(owner(), UUID_BYTES).bind(preset.name()).bind(parseToString(preset)))
                        .insert());
    }


    @Override
    public CompletableFuture<Collection<Brush>> all() {
        return CompletableFuture.supplyAsync(() ->
                query("SELECT uuid, name, brush FROM brushes WHERE uuid = ?")
                        .single(call().bind(owner(), UUID_BYTES))
                        .map(resultSet -> parseToObject(resultSet.getString("brush"), Brush.class))
                        .all());
    }

    @Override
    public CompletableFuture<Boolean> remove(String name) {
        return CompletableFuture.supplyAsync(() ->
                query("DELETE FROM brushes WHERE uuid = ? AND name LIKE ?")
                        .single(call().bind(owner(), UUID_BYTES).bind(name))
                        .delete()
                        .changed());
    }

    @Override
    protected CompletableFuture<List<String>> retrieveNames() {
        return CompletableFuture.supplyAsync(() ->
                query("SELECT name FROM brushes WHERE uuid = ?")
                        .single(call().bind(owner(), UUID_BYTES))
                        .map(resultSet -> resultSet.getString("name"))
                        .all());
    }

    @Override
    public CompletableFuture<Integer> size() {
        return CompletableFuture.supplyAsync(() -> query("SELECT count(1) AS count FROM brushes WHERE uuid = ?")
                .single(call().bind(owner(), UUID_BYTES))
                .map(rs -> rs.getInt("count"))
                .first()
                .orElse(0));
    }
}
