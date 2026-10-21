/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package de.eldoria.sbrdatabase.configuration.elements;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public class ConfigFile {
    private boolean checkUpdates = true;
    private Storages storages = new Storages();
    private Cache cache = new Cache();

    public Storages storages() {
        return storages;
    }

    public Cache cache() {
        return cache;
    }

    public boolean checkUpdates() {
        return checkUpdates;
    }
}
