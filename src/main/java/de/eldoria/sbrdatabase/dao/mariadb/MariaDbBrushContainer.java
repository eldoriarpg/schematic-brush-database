/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.mariadb;

import de.chojo.sqlutil.base.QueryFactoryHolder;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.mysql.MySqlBrushContainer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MariaDbBrushContainer extends MySqlBrushContainer {

    public MariaDbBrushContainer(@Nullable UUID uuid, Configuration configuration, QueryFactoryHolder factoryHolder) {
        super(uuid, configuration, factoryHolder);
    }
}
