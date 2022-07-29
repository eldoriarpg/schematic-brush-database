/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.sbrdatabase.dao.base;

import de.eldoria.sbrdatabase.SbrDatabase;
import de.eldoria.schematicbrush.storage.ContainerPagedAccess;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class DbContainerPagedAccess<T> implements ContainerPagedAccess<T> {
    private final BaseContainer<T> container;
    private final int size;

    public DbContainerPagedAccess(BaseContainer<T> container, int size) {
        this.container = container;
        this.size = size;
    }

    @Override
    public int size() {
        container.size().thenApply(size -> this.size);
        return size;
    }

    @Override
    public CompletableFuture<List<T>> page(int page, int size) {
        return container.page(page, size).exceptionally(err -> {
            SbrDatabase.logger().log(Level.SEVERE, "Could not retrieve page", err);
            return Collections.emptyList();
        });
    }
}
