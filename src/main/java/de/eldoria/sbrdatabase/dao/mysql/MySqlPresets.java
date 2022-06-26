/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.mysql;

import de.chojo.sqlutil.conversion.UUIDConverter;
import de.eldoria.sbrdatabase.dao.base.BasePresets;
import de.eldoria.schematicbrush.storage.preset.PresetContainer;

import javax.sql.DataSource;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class MySqlPresets extends BasePresets {
    public MySqlPresets(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public CompletableFuture<Map<UUID, ? extends PresetContainer>> playerContainers() {
        return builder(UUID.class).query("""
                        SELECT uuid
                        FROM presets
                        WHERE uuid IS NOT NULL
                        """)
                .emptyParams()
                .readRow(resultSet -> UUIDConverter.convert(resultSet.getBytes("uuid")))
                .all()
                .thenApply(uuids -> uuids.stream().collect(Collectors.toMap(uuid -> uuid, this::playerContainer)));
    }

    @Override
    public CompletableFuture<Integer> count() {
        return builder(Integer.class).query("SELECT COUNT(1) FROM presets;")
                .emptyParams()
                .readRow(rs -> rs.getInt("count"))
                .first()
                .thenApply(res -> res.orElse(0));
    }

    @Override
    public PresetContainer getContainer(UUID uuid) {
        return new MySqlPresetContainer(uuid, this);
    }
}
