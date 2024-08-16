/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.mysql;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.base.BasePresets;
import de.eldoria.schematicbrush.storage.preset.PresetContainer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static de.chojo.sadu.queries.converter.StandardValueConverter.UUID_BYTES;

public class MySqlPresets extends BasePresets {
    private final ObjectMapper mapper;

    public MySqlPresets(QueryConfiguration queryConfiguration, Configuration configuration, ObjectMapper mapper) {
        super(queryConfiguration, configuration);
        this.mapper = mapper;
    }

    @Override
    public CompletableFuture<Map<UUID, ? extends PresetContainer>> playerContainers() {
        return CompletableFuture.supplyAsync(() ->
                query("""
                        SELECT DISTINCT uuid
                        FROM presets
                        WHERE uuid IS NOT NULL
                        """)
                        .single()
                        .map(row -> row.get("uuid", UUID_BYTES))
                        .all()
                        .stream()
                        .collect(Collectors.toMap(uuid -> uuid, this::playerContainer))
        );
    }

    @Override
    public CompletableFuture<Integer> count() {
        return CompletableFuture.supplyAsync(() -> query("SELECT count(1) AS count FROM presets;")
                .single()
                .map(rs -> rs.getInt("count"))
                .first()
                .orElse(0));
    }

    @Override
    public PresetContainer getContainer(UUID uuid) {
        return new MySqlPresetContainer(uuid, configuration(), queryConfiguration(), mapper);
    }
}
