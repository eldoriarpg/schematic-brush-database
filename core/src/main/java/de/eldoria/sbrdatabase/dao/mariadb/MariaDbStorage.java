/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.mariadb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.base.BaseStorage;

public class MariaDbStorage extends BaseStorage {
    public MariaDbStorage(QueryConfiguration queryConfiguration, Configuration configuration, ObjectMapper mapper) {
        super(new MariaDbPresets(queryConfiguration, configuration, mapper), new MariaDbBrushes(queryConfiguration, configuration, mapper), queryConfiguration);
    }
}
