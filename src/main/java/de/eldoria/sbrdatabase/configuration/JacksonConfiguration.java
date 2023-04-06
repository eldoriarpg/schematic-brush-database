package de.eldoria.sbrdatabase.configuration;

import de.eldoria.eldoutilities.config.ConfigKey;
import de.eldoria.eldoutilities.config.JacksonConfig;
import de.eldoria.sbrdatabase.configuration.elements.Cache;
import de.eldoria.sbrdatabase.configuration.elements.ConfigFile;
import de.eldoria.sbrdatabase.configuration.elements.Storages;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class JacksonConfiguration extends JacksonConfig<ConfigFile> implements Configuration {
    public JacksonConfiguration(@NotNull Plugin plugin) {
        super(plugin, ConfigKey.defaultConfig(ConfigFile.class, ConfigFile::new));
    }

    @Override
    public Storages storages() {
        return main().storages();
    }

    @Override
    public Cache cache() {
        return main().cache();
    }
}
