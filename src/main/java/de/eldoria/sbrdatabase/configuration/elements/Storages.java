/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package de.eldoria.sbrdatabase.configuration.elements;

import de.eldoria.sbrdatabase.configuration.elements.storages.BaseDbConfig;
import de.eldoria.sbrdatabase.configuration.elements.storages.PostgresDbConfig;
import de.eldoria.schematicbrush.brush.config.util.Nameable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public class Storages {
    private List<String> activeTypes = new ArrayList<>();
    private BaseDbConfig mysql = new BaseDbConfig();
    private BaseDbConfig mariadb = new BaseDbConfig();
    private PostgresDbConfig postgres = new PostgresDbConfig();

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
