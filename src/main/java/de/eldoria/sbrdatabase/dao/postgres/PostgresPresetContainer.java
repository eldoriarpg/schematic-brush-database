/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package de.eldoria.sbrdatabase.dao.postgres;

import tools.jackson.databind.ObjectMapper;
import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import de.eldoria.sbrdatabase.configuration.Configuration;
import de.eldoria.sbrdatabase.dao.base.NameMatch;
import de.eldoria.sbrdatabase.dao.mysql.MySqlPresetContainer;
import de.eldoria.schematicbrush.storage.preset.Preset;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static de.chojo.sadu.queries.api.call.Call.call;
import static de.chojo.sadu.queries.converter.StandardValueConverter.UUID_BYTES;

public class PostgresPresetContainer extends MySqlPresetContainer {

    public PostgresPresetContainer(@Nullable UUID uuid, Configuration configuration, QueryConfiguration queryConfiguration, ObjectMapper mapper) {
        super(uuid, configuration, queryConfiguration, mapper);
    }

    @Override
    public CompletableFuture<Void> add(Preset preset) {
        return CompletableFuture.runAsync(() -> query("INSERT INTO presets(uuid, name, preset) VALUES(?, ?, ?) ON CONFLICT(uuid, name) DO UPDATE SET preset = excluded.preset")
                .single(call().bind(owner(), UUID_BYTES).bind(preset.name()).bind(parseToString(preset)))
                .insert());
    }

    @Override
    protected NameMatch nameMatch() {
        return NameMatch.POSTGRES;
    }
}
