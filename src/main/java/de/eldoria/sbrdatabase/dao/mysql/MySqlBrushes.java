/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.mysql;

import de.eldoria.sbrdatabase.dao.base.BaseBrushes;
import de.eldoria.sbrdatabase.dao.base.BasePresets;
import de.eldoria.schematicbrush.storage.brush.BrushContainer;
import de.eldoria.schematicbrush.storage.preset.PresetContainer;
import org.bukkit.entity.Player;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySqlBrushes extends BaseBrushes {
    public MySqlBrushes(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public CompletableFuture<Map<UUID, ? extends BrushContainer>> playerContainers() {
        return null;
    }

    @Override
    public CompletableFuture<Integer> count() {
        return null;
    }

    @Override
    public BrushContainer getContainer(UUID uuid) {
        return new MySqlBrushContainer(uuid, this);
    }
}
