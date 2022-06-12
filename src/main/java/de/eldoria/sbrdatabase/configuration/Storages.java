/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.configuration;

import de.chojo.sqlutil.databases.SqlType;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Storages implements ConfigurationSerializable {
    private List<String> activeTypes = new ArrayList<>();
    private BaseDbConfig mysql = new BaseDbConfig();
    private BaseDbConfig mariadb = new BaseDbConfig();
    private PostgresDbConfig postgres = new PostgresDbConfig();

    public Storages() {
    }

    public Storages(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        activeTypes = map.getValue("activeTypes");
        mysql = map.getValue("mysql");
        mariadb = map.getValue("mariadb");
        postgres = map.getValue("postgres");
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .build();
    }

    public List<String> activeTypes() {
        return activeTypes;
    }

    public BaseDbConfig mysql() {
        return mysql;
    }

    public BaseDbConfig mariadb() {
        return mariadb;
    }

    public PostgresDbConfig postgres() {
        return postgres;
    }

    public boolean isActive(SqlType type) {
        return activeTypes.stream().anyMatch(t -> t.equalsIgnoreCase(type.getName()));
    }
}
