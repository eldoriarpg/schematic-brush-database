/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.mariadb;

import com.zaxxer.hikari.HikariDataSource;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.base.BaseStorage;

public class MariaDbStorage extends BaseStorage {
    public MariaDbStorage(HikariDataSource dataSource, Configuration configuration) {
        super(new MariaDbPresets(dataSource, configuration), new MariaDbBrushes(dataSource, configuration), dataSource);
    }
}
