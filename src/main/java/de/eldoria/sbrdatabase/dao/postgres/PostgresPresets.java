/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.postgres;

import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.mariadb.MariaDbPresets;
import de.eldoria.schematicbrush.storage.preset.PresetContainer;
import de.eldoria.schematicbrush.storage.preset.Presets;

import javax.sql.DataSource;
import java.util.UUID;

public class PostgresPresets extends MariaDbPresets implements Presets {
    public PostgresPresets(DataSource dataSource, Configuration configuration) {
        super(dataSource, configuration);
    }

    @Override
    public PresetContainer getContainer(UUID uuid) {
        return new PostgresPresetContainer(uuid, configuration(), this);
    }
}
