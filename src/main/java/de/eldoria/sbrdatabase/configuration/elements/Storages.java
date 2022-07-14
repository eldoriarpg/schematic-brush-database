/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.configuration.elements;

import de.chojo.sqlutil.databases.SqlType;
import de.eldoria.eldoutilities.serialization.SerializationUtil;
import de.eldoria.sbrdatabase.configuration.elements.storages.BaseDbConfig;
import de.eldoria.sbrdatabase.configuration.elements.storages.PostgresDbConfig;
import de.eldoria.schematicbrush.brush.config.util.Nameable;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SerializableAs("sbdStorages")
public class Storages implements ConfigurationSerializable {
    private List<String> activeTypes = new ArrayList<>();
    private BaseDbConfig mysql = new BaseDbConfig();
    private BaseDbConfig mariadb = new BaseDbConfig();
    private PostgresDbConfig postgres = new PostgresDbConfig();

    public Storages() {
    }

    public Storages(Map<String, Object> objectMap) {
        var map = SerializationUtil.mapOf(objectMap);
        activeTypes = map.getValueOrDefault("activeTypes", Collections.emptyList());
        mysql = map.getValueOrDefault("mysql", new BaseDbConfig());
        mariadb = map.getValueOrDefault("mariadb", new BaseDbConfig());
        postgres = map.getValueOrDefault("postgres", new PostgresDbConfig());
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        return SerializationUtil.newBuilder()
                .add("activeTypes", activeTypes)
                .add("mysql", mysql)
                .add("mariadb", mariadb)
                .add("postgres", postgres)
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

    public boolean isActive(Nameable type) {
        return activeTypes.stream().anyMatch(t -> t.equalsIgnoreCase(type.name()));
    }
}
