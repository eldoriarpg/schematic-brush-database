/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package de.eldoria.sbrdatabase.configuration.elements.storages;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public class PostgresDbConfig extends BaseDbConfig {
    private String schema = "public";

    public PostgresDbConfig() {
        port = "5432";
    }

    public String schema() {
        return schema;
    }
}
