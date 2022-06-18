/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.mariadb;

import de.chojo.sqlutil.base.QueryFactoryHolder;
import de.eldoria.sbrdatabase.dao.base.BaseContainer;
import de.eldoria.sbrdatabase.dao.mysql.MySqlPresetContainer;
import de.eldoria.schematicbrush.storage.preset.Preset;
import de.eldoria.schematicbrush.storage.preset.PresetContainer;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MariaDbPresetContainer extends MySqlPresetContainer {

    public MariaDbPresetContainer(UUID uuid, QueryFactoryHolder factoryHolder) {
        super(uuid, factoryHolder);
    }
}
