/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.mariadb;

import de.eldoria.sbrdatabase.dao.mysql.MySqlBrushes;
import de.eldoria.schematicbrush.storage.brush.BrushContainer;

import javax.sql.DataSource;
import java.util.UUID;

public class MariaDbBrushes extends MySqlBrushes {
    public MariaDbBrushes(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public BrushContainer getContainer(UUID uuid) {
        return new MariaDbBrushContainer(uuid, this);
    }
}
