/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.mariadb;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.mysql.MySqlPresets;
import de.eldoria.schematicbrush.storage.preset.PresetContainer;

import javax.sql.DataSource;
import java.util.UUID;

public class MariaDbPresets extends MySqlPresets {

    private final ObjectMapper mapper;

    public MariaDbPresets(DataSource dataSource, Configuration configuration, ObjectMapper mapper) {
        super(dataSource, configuration, mapper);
        this.mapper = mapper;
    }

    @Override
    public PresetContainer getContainer(UUID uuid) {
        return new MariaDbPresetContainer(uuid, configuration(), this, mapper);
    }
}
