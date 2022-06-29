/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.configuration.elements.storages;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SerializableAs("sbdPostgresDbConfig")
public class PostgresDbConfig extends BaseDbConfig {

    private String schema = "public";

    public PostgresDbConfig() {
        port = "5432";
    }

    public PostgresDbConfig(Map<String, Object> objectMap) {
        super(objectMap);
        schema = SerializationUtil.mapOf(objectMap).getValue("schema");
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return SerializationUtil.newBuilder(super.serialize())
                .add("schema", schema)
                .build();
    }

    public String schema() {
        return schema;
    }
}
