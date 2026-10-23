/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package de.eldoria.sbrdatabase.dao.postgres;

import de.chojo.sadu.queries.api.configuration.QueryConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.sqlite.SQLiteDataSource;
import tools.jackson.databind.json.JsonMapper;

import java.nio.file.Path;
import java.util.UUID;

import static de.chojo.sadu.queries.api.call.Call.call;
import static de.chojo.sadu.queries.converter.StandardValueConverter.UUID_BYTES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Runs the name matching of the PostgreSQL containers against SQLite, which understands the same SQL for these
 * queries.
 */
class PostgresPresetContainerTest {
    private static final UUID OWNER = UUID.randomUUID();
    private QueryConfiguration queries;
    private PostgresPresetContainer container;

    @BeforeEach
    void setUp(@TempDir Path dir) {
        var dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:" + dir.resolve("presets.db"));
        queries = QueryConfiguration.builder(dataSource).setThrowExceptions(true).build();
        queries.query("CREATE TABLE presets(uuid BLOB NOT NULL, name TEXT NOT NULL, preset TEXT NOT NULL, UNIQUE (uuid, name))")
                .single()
                .insert();
        container = new PostgresPresetContainer(OWNER, null, queries, JsonMapper.builder().build());
    }

    @Test
    void percentIsNoWildcard() {
        store("tree");

        assertTrue(container.get("%").join().isEmpty());
        assertFalse(container.remove("%").join());
        assertEquals(1, container.size().join());
    }

    @Test
    void underscoreIsNoWildcard() {
        store("a_b");
        store("axb");

        assertTrue(container.remove("a_b").join());
        assertEquals(1, container.size().join());
        assertTrue(container.remove("axb").join());
    }

    @Test
    void caseIsIgnored() {
        store("Tree");

        assertTrue(container.remove("tREE").join());
        assertEquals(0, container.size().join());
    }

    private void store(String name) {
        queries.query("INSERT INTO presets(uuid, name, preset) VALUES (?, ?, '{}')")
                .single(call().bind(OWNER, UUID_BYTES).bind(name))
                .insert();
    }
}
