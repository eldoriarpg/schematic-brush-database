/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package de.eldoria.sbrdatabase.dao.postgres;

import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.base.BaseStorage;
import tools.jackson.databind.ObjectMapper;

public class PostgresStorage extends BaseStorage {
    public PostgresStorage(QueryConfiguration queryConfiguration, Configuration configuration, ObjectMapper mapper) {
        super(
                new PostgresPresets(queryConfiguration, configuration, mapper),
                new PostgresBrushes(queryConfiguration, configuration, mapper),
                queryConfiguration);
    }
}
