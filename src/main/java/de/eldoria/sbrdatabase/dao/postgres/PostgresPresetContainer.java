/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.postgres;

import de.chojo.sqlutil.base.QueryFactoryHolder;
import de.eldoria.sbrdatabase.dao.mysql.MySqlPresetContainer;
import de.eldoria.schematicbrush.storage.preset.Preset;

import java.util.Optional;
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

    @Override
    public CompletableFuture<Optional<Preset>> get(String name) {
        return builder(Preset.class).query("SELECT preset FROM presets WHERE uuid = ? AND name LIKE ?")
                .paramsBuilder(stmt -> stmt.setBytes(uuidBytes())
                        .setString(name))
                .readRow(resultSet -> yamlToObject(resultSet.getString("preset"), Preset.class))
                .first();
    }

    @Override
    public CompletableFuture<Boolean> remove(String name) {
        return builder(Boolean.class).query("DELETE FROM presets WHERE uuid = ? AND name ILIKE ?")
                .paramsBuilder(stmt -> stmt.setBytes(uuidBytes())
                        .setString(name))
                .delete()
                .execute()
                .thenApply(i -> i == 1);
    }
}
