/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.configuration.elements.storages;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@SerializableAs("sbdDbConfig")
public class BaseDbConfig implements ConfigurationSerializable {
    protected String host;
    protected String port;
    protected String database;
    protected String user;
    protected String password;
    protected int connections;

    public BaseDbConfig() {
        connections = 3;
        password = "passy";
        user = "root";
        database = "public";
        port = "3306";
        host = "localhost";
    }

    public BaseDbConfig(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        host = map.getValueOrDefault("host", "localhost");
        port = map.getValueOrDefault("port", "3306");
        database = map.getValueOrDefault("database", "public");
        user = map.getValueOrDefault("user", "root");
        password = map.getValueOrDefault("password", "passy");
        connections = map.getValueOrDefault("connections", 3);
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("host", host)
                .add("port", port)
                .add("database", database)
                .add("user", user)
                .add("password", password)
                .add("connections", connections)
                .build();
    }

    public String host() {
        return host;
    }

    public String port() {
        return port;
    }

    public String database() {
        return database;
    }

    public String user() {
        return user;
    }

    public String password() {
        return password;
    }

    public int connections() {
        return connections;
    }
}
