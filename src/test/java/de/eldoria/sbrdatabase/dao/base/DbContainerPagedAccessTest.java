/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package de.eldoria.sbrdatabase.dao.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DbContainerPagedAccessTest {
    @Test
    void sizeIsTheOneCountedWhenCreated() {
        var access = new DbContainerPagedAccess<String>(null, 7);

        assertEquals(7, access.size());
    }
}
