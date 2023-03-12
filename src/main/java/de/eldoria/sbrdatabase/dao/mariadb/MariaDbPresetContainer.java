/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.mariadb;

import de.chojo.sadu.base.QueryFactory;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.mysql.MySqlPresetContainer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MariaDbPresetContainer extends MySqlPresetContainer {

    public MariaDbPresetContainer(@Nullable UUID uuid, Configuration configuration, QueryFactory factoryHolder) {
        super(uuid, configuration, factoryHolder);
    }
}
