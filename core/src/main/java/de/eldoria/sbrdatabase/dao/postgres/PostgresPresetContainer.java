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
import de.eldoria.sbrdatabase.dao.mysql.MySqlPresetContainer;
import de.eldoria.schematicbrush.storage.preset.Preset;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PostgresPresetContainer extends MySqlPresetContainer {

    public PostgresPresetContainer(@Nullable UUID uuid, Configuration configuration, QueryFactory factoryHolder, ObjectMapper mapper) {
        super(uuid, configuration, factoryHolder, mapper);
    }

    @Override
    public CompletableFuture<Void> add(Preset preset) {
        return builder().query("INSERT INTO presets(uuid, name, preset) VALUES(?, ?, ?) ON CONFLICT(uuid, name) DO UPDATE SET preset = excluded.preset")
                .parameter(stmt ->
                        stmt.setUuidAsBytes(owner())
                                .setString(preset.name())
                                .setString(parseToString(preset))).insert().execute().thenApply(r -> null);
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
    public CompletableFuture<Boolean> remove(String name) {
        return builder(Boolean.class).query("DELETE FROM presets WHERE uuid = ? AND name ILIKE ?")
                .parameter(stmt -> stmt.setUuidAsBytes(owner())
                        .setString(name))
                .delete()
                .send()
                .thenApply(UpdateResult::changed);
    }
}
