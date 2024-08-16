/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.mysql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.base.BaseStorage;

public class MySqlStorage extends BaseStorage {
    public MySqlStorage(QueryConfiguration config, Configuration configuration, ObjectMapper mapper) {
        super(new MySqlPresets(config, configuration, mapper), new MySqlBrushes(config, configuration, mapper), config);
    }

}
