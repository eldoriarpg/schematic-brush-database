/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.mysql;

import com.zaxxer.hikari.HikariDataSource;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.base.BaseStorage;

public class MySqlStorage extends BaseStorage {
    public MySqlStorage(HikariDataSource dataSource, Configuration configuration) {
        super(new MySqlPresets(dataSource, configuration), new MySqlBrushes(dataSource, configuration), dataSource);
    }

}
