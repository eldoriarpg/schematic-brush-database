/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.base;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.chojo.sqlutil.base.QueryFactoryHolder;
import de.chojo.sqlutil.conversion.UUIDConverter;
import de.eldoria.eldoutilities.utils.Futures;
import de.eldoria.sbrdatabase.SbrDatabase;
import de.eldoria.schematicbrush.storage.preset.Preset;
import de.eldoria.schematicbrush.storage.preset.PresetContainer;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public abstract class BasePresetContainer extends QueryFactoryHolder implements PresetContainer {
    private final UUID uuid;
    private Set<String> names = Collections.emptySet();
    private Instant lastRefresh = Instant.MIN;

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
            .setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE)
            .enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE, "clazz");

    public BasePresetContainer(UUID uuid, QueryFactoryHolder factoryHolder) {
        super(factoryHolder);
        this.uuid = uuid;
    }

    protected Preset jsonToPreset(String preset) {
        try {
            return MAPPER.readValue(preset, Preset.class);
        } catch (IOException e) {
            SbrDatabase.logger().log(Level.SEVERE, "Could not deserialize preset", e);
            return null;
        }
    }

    protected String presetToJson(Preset preset) {
        try {
            return MAPPER.writeValueAsString(preset);
        } catch (IOException e) {
            SbrDatabase.logger().log(Level.SEVERE, "Could not serialize preset", e);
            return null;
        }
    }

    @Override
    public final Set<String> names() {
        // TODO: Make refresh time configurable.
        if (lastRefresh.isBefore(Instant.now().minus(30, ChronoUnit.SECONDS))) {
            lastRefresh = Instant.now();
            retrieveNames()
                    .whenComplete(Futures.whenComplete(
                            names -> this.names = new HashSet<>(names),
                            err -> SbrDatabase.logger().log(Level.SEVERE, "Could not refresh names", err)));
        }
        return names;
    }

    protected abstract CompletableFuture<List<String>> retrieveNames();

    public UUID uuid() {
        return uuid;
    }
    public byte[] uuidBytes() {
        return UUIDConverter.convert(uuid);
    }
}
