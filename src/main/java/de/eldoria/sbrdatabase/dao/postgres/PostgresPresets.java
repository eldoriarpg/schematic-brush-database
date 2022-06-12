/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.postgres;

import de.eldoria.sbrdatabase.dao.mariadb.MariaDbPresets;

import javax.sql.DataSource;

public class PostgresPresets extends MariaDbPresets {
    public PostgresPresets(DataSource dataSource) {
        super(dataSource);
    }
}
