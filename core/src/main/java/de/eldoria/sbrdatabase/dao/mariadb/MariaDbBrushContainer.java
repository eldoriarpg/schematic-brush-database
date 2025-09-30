/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package de.eldoria.sbrdatabase.dao.mariadb;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.mysql.MySqlBrushContainer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MariaDbBrushContainer extends MySqlBrushContainer {

    public MariaDbBrushContainer(@Nullable UUID uuid, Configuration configuration, QueryConfiguration queryConfiguration, ObjectMapper mapper) {
        super(uuid, configuration, queryConfiguration, mapper);
    }
}
