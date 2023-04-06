/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.postgres;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.base.BaseStorage;

public class PostgresStorage extends BaseStorage {
    public PostgresStorage(HikariDataSource dataSource, Configuration configuration, ObjectMapper mapper) {
        super(new PostgresPresets(dataSource, configuration, mapper), new PostgresBrushes(dataSource, configuration, mapper), dataSource);
    }

}
