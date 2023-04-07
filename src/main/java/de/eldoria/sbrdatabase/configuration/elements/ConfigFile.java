/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.configuration.elements;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ConfigFile {
    private Storages storages;
    private Cache cache;

    public ConfigFile() {
        this.storages = new Storages();
        this.cache = new Cache();
    }

    public Storages storages() {
        return storages;
    }

    public Cache cache() {
        return cache;
    }

    public void storages(Storages storages) {
        this.storages = storages;
    }

    public void cache(Cache cache) {
        this.cache = cache;
    }
}
