/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.postgres;

import com.zaxxer.hikari.HikariDataSource;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.base.BaseStorage;
import de.eldoria.sbrdatabase.dao.mysql.MySqlBrushes;
import de.eldoria.sbrdatabase.dao.mysql.MySqlPresets;

public class PostgresStorage extends BaseStorage {
    public PostgresStorage(HikariDataSource dataSource, Configuration configuration) {
        super(new PostgresPresets(dataSource, configuration), new PostgresBrushes(dataSource, configuration), dataSource);
    }

}
