/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.postgres;

import de.chojo.sqlutil.base.QueryFactoryHolder;
import de.eldoria.sbrdatabase.dao.mysql.MySqlPresetContainer;
import de.eldoria.schematicbrush.storage.preset.Preset;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PostgresPresetContainer extends MySqlPresetContainer {

    public PostgresPresetContainer(UUID uuid, QueryFactoryHolder factoryHolder) {
        super(uuid, factoryHolder);
    }

    @Override
    public CompletableFuture<Void> add(Preset preset) {
        return builder().query("INSERT INTO presets(uuid, name, preset) VALUES(?, ?, ?) ON CONFLICT(uuid, name) DO UPDATE SET preset = excluded.preset")
                .paramsBuilder(stmt ->
                        stmt.setBytes(uuidBytes())
                                .setString(preset.name())
                                .setString(presetToYaml(preset))).insert().execute().thenApply(r -> null);
    }
}
