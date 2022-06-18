/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.postgres;

import de.chojo.sqlutil.base.QueryFactoryHolder;
import de.eldoria.sbrdatabase.dao.mysql.MySqlBrushContainer;
import de.eldoria.sbrdatabase.dao.mysql.MySqlPresetContainer;
import de.eldoria.schematicbrush.storage.brush.Brush;
import de.eldoria.schematicbrush.storage.preset.Preset;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PostgresBrushContainer extends MySqlBrushContainer {

    public PostgresBrushContainer(UUID uuid, QueryFactoryHolder factoryHolder) {
        super(uuid, factoryHolder);
    }

    @Override
    public CompletableFuture<Void> add(Brush preset) {
        return builder().query("INSERT INTO brushes(uuid, name, brush) VALUES(?, ?, ?) ON CONFLICT(uuid, name) DO UPDATE SET brush = excluded.brush")
                .paramsBuilder(stmt ->
                        stmt.setString(presetToJson(preset))
                                .setBytes(uuidBytes())
                                .setString(preset.name())
                                .setString(presetToJson(preset)))
                .insert()
                .execute()
                .thenApply(r -> null);
    }
}
