/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.configuration;

import de.eldoria.eldoutilities.configuration.EldoConfig;
import de.eldoria.sbrdatabase.configuration.elements.Cache;
import de.eldoria.sbrdatabase.configuration.elements.Storages;
import org.bukkit.plugin.Plugin;

public class LegacyConfiguration extends EldoConfig implements Configuration {
    private Storages storages;
    private Cache cache;

    public LegacyConfiguration(Plugin plugin) {
        super(plugin);
    }

    @Override
    protected void reloadConfigs() {
        storages = getConfig().getObject("storages", Storages.class, new Storages());
        cache = getConfig().getObject("cache", Cache.class, new Cache());
    }

    @Override
    protected void saveConfigs() {
        getConfig().set("storages", storages);
        getConfig().set("cache", cache);
    }

    @Override
    public Storages storages() {
        return storages;
    }

    @Override
    public Cache cache() {
        return cache;
    }
}
