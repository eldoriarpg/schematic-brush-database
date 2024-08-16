/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.base;

import com.zaxxer.hikari.HikariDataSource;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.eldoria.schematicbrush.storage.Storage;
import de.eldoria.schematicbrush.storage.brush.Brushes;
import de.eldoria.schematicbrush.storage.preset.Presets;

public class BaseStorage implements Storage {
    private final BasePresets presets;
    private final BaseBrushes brushes;
    private final QueryConfiguration queryConfiguration;

    public BaseStorage(BasePresets presets, BaseBrushes brushes, QueryConfiguration queryConfiguration) {
        this.presets = presets;
        this.brushes = brushes;
        this.queryConfiguration = queryConfiguration;
    }

    @Override
    public Presets presets() {
        return presets;
    }

    @Override
    public Brushes brushes() {
        return brushes;
    }

    @Override
    public void shutdown() {
        ((HikariDataSource) queryConfiguration.dataSource()).close();
    }
}
