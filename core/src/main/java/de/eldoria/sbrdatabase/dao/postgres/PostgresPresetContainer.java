/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.postgres;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.mysql.MySqlPresetContainer;
import de.eldoria.schematicbrush.storage.preset.Preset;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static de.chojo.sadu.queries.api.call.Call.call;
import static de.chojo.sadu.queries.converter.StandardValueConverter.UUID_BYTES;

public class PostgresPresetContainer extends MySqlPresetContainer {

    public PostgresPresetContainer(@Nullable UUID uuid, Configuration configuration, QueryConfiguration queryConfiguration, ObjectMapper mapper) {
        super(uuid, configuration, queryConfiguration, mapper);
    }

    @Override
    public CompletableFuture<Void> add(Preset preset) {
        return CompletableFuture.runAsync(() -> query("INSERT INTO presets(uuid, name, preset) VALUES(?, ?, ?) ON CONFLICT(uuid, name) DO UPDATE SET preset = excluded.preset")
                .single(call().bind(owner(), UUID_BYTES).bind(preset.name()).bind(parseToString(preset)))
                .insert());
    }

    @Override
    public CompletableFuture<Optional<Preset>> get(String name) {
        return CompletableFuture.supplyAsync(() -> query("SELECT preset FROM presets WHERE uuid = ? AND name LIKE ?")
                .single(call().bind(owner(), UUID_BYTES).bind(name))
                .map(resultSet -> parseToObject(resultSet.getString("preset"), Preset.class))
                .first());
    }

    @Override
    public CompletableFuture<Boolean> remove(String name) {
        return CompletableFuture.supplyAsync(() -> query("DELETE FROM presets WHERE uuid = ? AND name ILIKE ?")
                .single(call().bind(owner(), UUID_BYTES).bind(name))
                .delete()
                .changed());
    }
}
