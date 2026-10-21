/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package de.eldoria.sbrdatabase.configuration.elements.storages;

@SuppressWarnings({"FieldMayBeFinal"})
public class BaseDbConfig {
    protected String host = "localhost";
    protected String port = "3306";
    protected String database = "public";
    protected String user = "root";
    protected String password = "passy";
    protected int connections = 3;

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
