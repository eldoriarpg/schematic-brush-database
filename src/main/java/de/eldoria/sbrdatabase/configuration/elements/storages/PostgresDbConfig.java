/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.configuration.elements.storages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonCreator
    public PostgresDbConfig(@JsonProperty("host") String host,
                            @JsonProperty("port") String port,
                            @JsonProperty("database") String database,
                            @JsonProperty("user") String user,
                            @JsonProperty("password") String password,
                            @JsonProperty("connections") int connections,
                            @JsonProperty("schema") String schema) {
        super(host, port, database, user, password, connections);
        this.schema = schema;
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
