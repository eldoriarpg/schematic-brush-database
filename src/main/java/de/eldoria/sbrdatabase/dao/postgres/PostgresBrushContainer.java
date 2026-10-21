/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package de.eldoria.sbrdatabase.dao.postgres;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.mysql.MySqlBrushContainer;
import de.eldoria.schematicbrush.storage.brush.Brush;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static de.chojo.sadu.queries.api.call.Call.call;
import static de.chojo.sadu.queries.converter.StandardValueConverter.UUID_BYTES;

public class PostgresBrushContainer extends MySqlBrushContainer {

    public PostgresBrushContainer(@Nullable UUID uuid, Configuration configuration, QueryConfiguration queryConfiguration, ObjectMapper mapper) {
        super(uuid, configuration, queryConfiguration, mapper);
    }

    @Override
    public CompletableFuture<Void> add(Brush preset) {
        return CompletableFuture.runAsync(() ->
                query("INSERT INTO brushes(uuid, name, brush) VALUES(?, ?, ?) ON CONFLICT(uuid, name) DO UPDATE SET brush = excluded.brush")
                        .single(call().bind(owner(), UUID_BYTES).bind(preset.name()).bind(parseToString(preset)))
                        .insert());
    }

    @Override
    public CompletableFuture<Optional<Brush>> get(String name) {
        return CompletableFuture.supplyAsync(() ->
                query("SELECT brush FROM brushes WHERE uuid = ? AND name ILIKE ?")
                        .single(call().bind(owner(), UUID_BYTES).bind(name))
                        .map(resultSet -> parseToObject(resultSet.getString("brush"), Brush.class))
                        .first());
    }

    @Override
    public CompletableFuture<Boolean> remove(String name) {
        return CompletableFuture.supplyAsync(() ->
                query("DELETE FROM brushes WHERE uuid = ? AND name ILIKE ?")
                        .single(call().bind(owner(), UUID_BYTES).bind(name))
                        .delete()
                        .changed());
    }
}
