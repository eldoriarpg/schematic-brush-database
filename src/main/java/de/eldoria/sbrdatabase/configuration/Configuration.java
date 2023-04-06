package de.eldoria.sbrdatabase.configuration;

import de.eldoria.sbrdatabase.configuration.elements.Cache;
import de.eldoria.sbrdatabase.configuration.elements.Storages;

public interface Configuration {
    Storages storages();

    Cache cache();
}
