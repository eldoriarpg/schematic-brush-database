/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package de.eldoria.sbrdatabase.configuration.elements;

import dev.chojo.ocular.Configurations;
import dev.chojo.ocular.dataformats.YamlDataFormat;
import dev.chojo.ocular.key.Key;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigFileTest {
    @TempDir
    Path dir;

    @Test
    void switchedOffUpdateChecksSilenceTheNotice() throws IOException {
        UpdateSettings updates = load("checkUpdates: false\n").main().updates();

        assertFalse(updates.notice());
        assertFalse(updates.autoUpdate());
        assertTrue(updates.askForAutoUpdate());
    }

    @Test
    void switchedOnUpdateChecksKeepTheNotice() throws IOException {
        assertTrue(load("checkUpdates: true\n").main().updates().notice());
    }

    @Test
    void theOldSwitchIsNotWrittenAgain() throws IOException {
        Configurations<ConfigFile> configurations = load("checkUpdates: false\n");
        configurations.main();
        configurations.save();

        String written = Files.readString(dir.resolve("config.yml"));
        assertFalse(written.contains("checkUpdates"));
        assertTrue(written.contains("notice: false"));
    }

    @Test
    void theUpdatesSectionIsRead() throws IOException {
        UpdateSettings updates = load("""
                updates:
                  notice: false
                  autoUpdate: true
                  askForAutoUpdate: false
                """).main().updates();

        assertFalse(updates.notice());
        assertTrue(updates.autoUpdate());
        assertFalse(updates.askForAutoUpdate());
    }

    private Configurations<ConfigFile> load(String yaml) throws IOException {
        Files.writeString(dir.resolve("config.yml"), yaml);
        Key<ConfigFile> key =
                Key.builder(Path.of("config.yml"), ConfigFile::new).build();
        return new Configurations<>(
                dir, key, List.of(new YamlDataFormat()), getClass().getClassLoader(), null);
    }
}
