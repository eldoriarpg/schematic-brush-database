/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.mysql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.base.BaseStorage;

public class MySqlStorage extends BaseStorage {
    public MySqlStorage(HikariDataSource dataSource, Configuration configuration, ObjectMapper mapper) {
        super(new MySqlPresets(dataSource, configuration, mapper), new MySqlBrushes(dataSource, configuration, mapper), dataSource);
    }

}
