/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.mariadb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.base.BaseStorage;

public class MariaDbStorage extends BaseStorage {
    public MariaDbStorage(HikariDataSource dataSource, Configuration configuration, ObjectMapper mapper) {
        super(new MariaDbPresets(dataSource, configuration, mapper), new MariaDbBrushes(dataSource, configuration, mapper), dataSource);
    }
}
