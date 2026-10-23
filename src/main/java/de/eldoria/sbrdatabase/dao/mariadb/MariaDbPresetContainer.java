/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package de.eldoria.sbrdatabase.dao.mariadb;

import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.mysql.MySqlPresetContainer;
import org.jetbrains.annotations.Nullable;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

public class MariaDbPresetContainer extends MySqlPresetContainer {

    public MariaDbPresetContainer(
            @Nullable UUID uuid,
            Configuration configuration,
            QueryConfiguration queryConfiguration,
            ObjectMapper mapper) {
        super(uuid, configuration, queryConfiguration, mapper);
    }
}
