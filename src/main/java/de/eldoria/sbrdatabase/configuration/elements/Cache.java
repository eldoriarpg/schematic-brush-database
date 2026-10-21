/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package de.eldoria.sbrdatabase.configuration.elements;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
public class Cache {
    private int cacheRefreshSec = 30;

    public int cacheRefreshSec() {
        return cacheRefreshSec;
    }
}
