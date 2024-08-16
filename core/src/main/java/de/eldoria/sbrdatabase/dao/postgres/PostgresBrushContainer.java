/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.postgres;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.chojo.sadu.base.QueryFactory;
import de.chojo.sadu.wrapper.util.UpdateResult;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.mysql.MySqlBrushContainer;
import de.eldoria.schematicbrush.storage.brush.Brush;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PostgresBrushContainer extends MySqlBrushContainer {

    public PostgresBrushContainer(@Nullable UUID uuid, Configuration configuration, QueryFactory factoryHolder, ObjectMapper mapper) {
        super(uuid, configuration, factoryHolder, mapper);
    }

    @Override
    public CompletableFuture<Void> add(Brush preset) {
        return builder().query("INSERT INTO brushes(uuid, name, brush) VALUES(?, ?, ?) ON CONFLICT(uuid, name) DO UPDATE SET brush = excluded.brush")
                .parameter(stmt ->
                        stmt.setUuidAsBytes(owner())
                                .setString(preset.name())
                                .setString(parseToString(preset)))
                .insert()
                .send()
                .thenApply(r -> null);
    }

    @Override
    public CompletableFuture<Optional<Brush>> get(String name) {
        return builder(Brush.class).query("SELECT brush FROM brushes WHERE uuid = ? AND name ILIKE ?")
                .parameter(stmt -> stmt.setUuidAsBytes(owner())
                        .setString(name))
                .readRow(resultSet -> parseToObject(resultSet.getString("brush"), Brush.class))
                .first();
    }

    @Override
    public CompletableFuture<Boolean> remove(String name) {
        return builder(Boolean.class).query("DELETE FROM brushes WHERE uuid = ? AND name ILIKE ?")
                .parameter(stmt -> stmt.setUuidAsBytes(owner())
                        .setString(name))
                .delete()
                .send()
                .thenApply(UpdateResult::changed);
    }
}
