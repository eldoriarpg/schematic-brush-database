/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.postgres;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.mariadb.MariaDbBrushes;
import de.eldoria.schematicbrush.storage.brush.BrushContainer;

import javax.sql.DataSource;
import java.util.UUID;

public class PostgresBrushes extends MariaDbBrushes {
    private final ObjectMapper mapper;

    public PostgresBrushes(QueryConfiguration queryConfiguration, Configuration configuration, ObjectMapper mapper) {
        super(queryConfiguration, configuration, mapper);
        this.mapper = mapper;
    }

    @Override
    public BrushContainer getContainer(UUID uuid) {
        return new PostgresBrushContainer(uuid, configuration(), queryConfiguration(), mapper);
    }
}
