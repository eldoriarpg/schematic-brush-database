/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.mariadb;

import de.eldoria.sbrdatabase.dao.mysql.MySqlPresets;

import javax.sql.DataSource;

public class MariaDbPresets extends MySqlPresets {
    public MariaDbPresets(DataSource dataSource) {
        super(dataSource);
    }

}
