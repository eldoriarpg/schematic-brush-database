package de.eldoria.sbrdatabase.configuration.elements;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ConfigFile {
    private Storages storages;
    private Cache cache;

    @JsonCreator
    public ConfigFile(@JsonProperty("storages") Storages storages,
                      @JsonProperty("cache") Cache cache) {
        this.storages = storages;
        this.cache = cache;
    }

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
