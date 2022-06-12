/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.configuration;

import de.eldoria.eldoutilities.serialization.SerializationUtil;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BaseDbConfig implements ConfigurationSerializable {
    private String host = "localhost";
    private String port = "3306";
    private String database = "public";
    private String user = "root";
    private String password = "passy";

    public BaseDbConfig() {
    }

    public BaseDbConfig(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        host = map.getValue("host");
        port = map.getValue("port");
        database = map.getValue("database");
        user = map.getValue("user");
        password = map.getValue("password");
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
}
