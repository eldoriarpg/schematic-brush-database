/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package de.eldoria.sbrdatabase.configuration.elements;

import com.fasterxml.jackson.annotation.JsonSetter;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public class ConfigFile {
    private UpdateSettings updates = new UpdateSettings();
    private Storages storages = new Storages();
    private Cache cache = new Cache();

    public UpdateSettings updates() {
        return updates;
    }

    public Storages storages() {
        return storages;
    }

    public Cache cache() {
        return cache;
    }

    /**
     * Reads the update switch of configs written before the {@code updates} section existed as the start value of
     * {@link UpdateSettings#notice()}. It is only read, so the next save drops the old key.
     *
     * @param checkUpdates whether update checks were on
     */
    @JsonSetter("checkUpdates")
    private void checkUpdates(boolean checkUpdates) {
        updates.notice(checkUpdates);
    }
}
