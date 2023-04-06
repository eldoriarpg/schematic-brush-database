/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.mysql;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.base.BaseBrushes;
import de.eldoria.schematicbrush.storage.brush.BrushContainer;

import javax.sql.DataSource;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class MySqlBrushes extends BaseBrushes {
    private final ObjectMapper mapper;

    public MySqlBrushes(DataSource dataSource, Configuration configuration, ObjectMapper mapper) {
        super(dataSource, configuration);
        this.mapper = mapper;
    }

    @Override
    public CompletableFuture<Map<UUID, ? extends BrushContainer>> playerContainers() {
        return builder(UUID.class).query("""
                        SELECT uuid
                        FROM brushes
                        WHERE uuid IS NOT NULL
                        """)
                .emptyParams()
                .readRow(resultSet -> resultSet.getUuidFromBytes("uuid"))
                .all()
                .thenApply(uuids -> uuids.stream().collect(Collectors.toMap(uuid -> uuid, this::playerContainer)));
    }

    @Override
    public CompletableFuture<Integer> count() {
        return builder(Integer.class).query("SELECT count(1) FROM brushes;")
                .emptyParams()
                .readRow(rs -> rs.getInt("count"))
                .first()
                .thenApply(res -> res.orElse(0));
    }

    @Override
    public BrushContainer getContainer(UUID uuid) {
        return new MySqlBrushContainer(uuid, configuration(), this, mapper);
    }
}
