/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.mysql;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.base.BaseBrushes;
import de.eldoria.schematicbrush.storage.brush.BrushContainer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static de.chojo.sadu.queries.converter.StandardValueConverter.UUID_BYTES;

public class MySqlBrushes extends BaseBrushes {
    private final ObjectMapper mapper;

    public MySqlBrushes(QueryConfiguration config, Configuration configuration, ObjectMapper mapper) {
        super(config, configuration);
        this.mapper = mapper;
    }

    @Override
    public CompletableFuture<Map<UUID, ? extends BrushContainer>> playerContainers() {
        return CompletableFuture.supplyAsync(() -> query("""
                SELECT DISTINCT uuid
                FROM brushes
                WHERE uuid IS NOT NULL
                """)
                .single()
                .map(resultSet -> resultSet.get("uuid", UUID_BYTES))
                .all()
                .stream()
                .collect(Collectors.toMap(uuid -> uuid, this::playerContainer)));
    }

    @Override
    public CompletableFuture<Integer> count() {
        return CompletableFuture.supplyAsync(() -> query("SELECT count(1) AS count FROM brushes;")
                .single()
                .map(rs -> rs.getInt("count"))
                .first()
                .orElse(0));
    }

    @Override
    public BrushContainer getContainer(UUID uuid) {
        return new MySqlBrushContainer(uuid, configuration(), queryConfiguration(), mapper);
    }
}
