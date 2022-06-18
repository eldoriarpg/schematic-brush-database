/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.storage;

import de.eldoria.schematicbrush.storage.Storage;
import de.eldoria.schematicbrush.storage.brush.Brushes;
import de.eldoria.schematicbrush.storage.preset.Presets;

public class BaseStorage implements Storage {
    private final Presets presets;
    private final Brushes brushes;

    public BaseStorage(Presets presets, Brushes brushes) {
        this.presets = presets;
        this.brushes = brushes;
    }

    @Override
    public Presets presets() {
        return presets;
    }

    @Override
    public Brushes brushes() {
        return brushes;
    }
}
