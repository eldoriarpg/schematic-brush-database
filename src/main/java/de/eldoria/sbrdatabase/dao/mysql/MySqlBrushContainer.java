/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.mysql;

import de.chojo.sqlutil.base.QueryFactoryHolder;
import de.eldoria.sbrdatabase.dao.base.BaseContainer;
import de.eldoria.schematicbrush.storage.brush.Brush;
import de.eldoria.schematicbrush.storage.brush.BrushContainer;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySqlBrushContainer extends BaseContainer implements BrushContainer {

    public MySqlBrushContainer(UUID uuid, QueryFactoryHolder factoryHolder) {
        super(uuid, factoryHolder);
    }

    @Override
    public CompletableFuture<Optional<Brush>> get(String name) {
        return builder(Brush.class).query("SELECT preset FROM presets WHERE uuid=? AND name=?")
                .paramsBuilder(paramBuilder -> paramBuilder
                        .setBytes(uuidBytes())
                        .setString(name)).readRow(resultSet ->
                        yamlToObject(resultSet.getString("preset"), Brush.class))
                .first();
    }

    @Override
    public CompletableFuture<Void> add(Brush preset) {
        return builder().query("INSERT INTO brushes(uuid, name, brush) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE brush = ?")
                .paramsBuilder(stmt ->
                        stmt.setString(presetToYaml(preset))
                                .setBytes(uuidBytes())
                                .setString(preset.name())
                                .setString(presetToYaml(preset))
                                .setString(presetToYaml(preset))).insert().execute().thenApply(r -> null);
    }


    @Override
    public CompletableFuture<Collection<Brush>> all() {
        return builder(Brush.class).queryWithoutParams("SELECT uuid, name, brush FROM brushes")
                .readRow(resultSet -> yamlToObject(resultSet.getString("preset"), Brush.class))
                .all()
                .thenApply(list -> list);
    }

    @Override
    public CompletableFuture<Boolean> remove(String name) {
        return builder(Boolean.class).query("DELETE FROM brushes WHERE uuid=? AND name=?")
                .paramsBuilder(paramBuilder ->
                        paramBuilder.setBytes(uuidBytes())
                                .setString(name)).delete().execute().thenApply(i -> i == 1);
    }

    @Override
    protected CompletableFuture<List<String>> retrieveNames() {
        return builder(String.class).queryWithoutParams("SELECT name FROM brushes")
                .readRow(resultSet -> resultSet.getString("name"))
                .all();
    }

    @Override
    public void close() throws IOException {
    }
}
