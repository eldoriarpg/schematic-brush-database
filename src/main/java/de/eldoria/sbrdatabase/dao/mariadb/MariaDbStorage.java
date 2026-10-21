/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package de.eldoria.sbrdatabase.dao.mariadb;

import tools.jackson.databind.ObjectMapper;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.base.BaseStorage;

public class MariaDbStorage extends BaseStorage {
    public MariaDbStorage(QueryConfiguration queryConfiguration, Configuration configuration, ObjectMapper mapper) {
        super(new MariaDbPresets(queryConfiguration, configuration, mapper), new MariaDbBrushes(queryConfiguration, configuration, mapper), queryConfiguration);
    }
}
