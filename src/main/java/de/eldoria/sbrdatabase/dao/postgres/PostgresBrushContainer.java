/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package de.eldoria.sbrdatabase.dao.postgres;

import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.base.NameMatch;
import de.eldoria.sbrdatabase.dao.mysql.MySqlBrushContainer;
import org.jetbrains.annotations.Nullable;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

public class PostgresBrushContainer extends MySqlBrushContainer {

    public PostgresBrushContainer(
            @Nullable UUID uuid,
            Configuration configuration,
            QueryConfiguration queryConfiguration,
            ObjectMapper mapper) {
        super(uuid, configuration, queryConfiguration, mapper);
    }

    @Override
    protected String upsert() {
        return "INSERT INTO brushes(uuid, name, brush) VALUES(?, ?, ?) ON CONFLICT(uuid, name) DO UPDATE SET brush = excluded.brush";
    }

    @Override
    protected NameMatch nameMatch() {
        return NameMatch.POSTGRES;
    }
}
