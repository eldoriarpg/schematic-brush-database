/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package de.eldoria.sbrdatabase.dao.postgres;

import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.base.NameMatch;
import de.eldoria.sbrdatabase.dao.mysql.MySqlPresetContainer;
import org.jetbrains.annotations.Nullable;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

public class PostgresPresetContainer extends MySqlPresetContainer {

    public PostgresPresetContainer(
            @Nullable UUID uuid,
            Configuration configuration,
            QueryConfiguration queryConfiguration,
            ObjectMapper mapper) {
        super(uuid, configuration, queryConfiguration, mapper);
    }

    @Override
    protected String upsert() {
        return "INSERT INTO presets(uuid, name, preset) VALUES(?, ?, ?) ON CONFLICT(uuid, name) DO UPDATE SET preset = excluded.preset";
    }

    @Override
    protected NameMatch nameMatch() {
        return NameMatch.POSTGRES;
    }
}
