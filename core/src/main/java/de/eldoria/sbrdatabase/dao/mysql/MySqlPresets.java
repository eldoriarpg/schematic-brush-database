/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.mysql;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.base.BasePresets;
import de.eldoria.schematicbrush.storage.preset.PresetContainer;

import javax.sql.DataSource;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class MySqlPresets extends BasePresets {
    private final ObjectMapper mapper;

    public MySqlPresets(DataSource dataSource, Configuration configuration, ObjectMapper mapper) {
        super(dataSource, configuration);
        this.mapper = mapper;
    }

    @Override
    public CompletableFuture<Map<UUID, ? extends PresetContainer>> playerContainers() {
        return builder(UUID.class).query("""
                        SELECT DISTINCT uuid
                        FROM presets
                        WHERE uuid IS NOT NULL
                        """)
                .emptyParams()
                .readRow(resultSet -> resultSet.getUuidFromBytes("uuid"))
                .all()
                .thenApply(uuids -> uuids.stream().collect(Collectors.toMap(uuid -> uuid, this::playerContainer)));
    }

    @Override
    public CompletableFuture<Integer> count() {
        return builder(Integer.class).query("SELECT count(1) AS count FROM presets;")
                .emptyParams()
                .readRow(rs -> rs.getInt("count"))
                .first()
                .thenApply(res -> res.orElse(0));
    }

    @Override
    public PresetContainer getContainer(UUID uuid) {
        return new MySqlPresetContainer(uuid, configuration(), this, mapper);
    }
}
