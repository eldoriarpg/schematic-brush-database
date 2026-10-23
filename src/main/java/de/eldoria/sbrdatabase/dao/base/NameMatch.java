/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) EldoriaRPG Team and Contributor
 */
package de.eldoria.sbrdatabase.dao.base;

/**
 * SQL conditions that match the {@code name} column against one bound name.
 * <p>
 * Names match exactly, so {@code %} and {@code _} have no special meaning, and ignore case, as the YAML storage of
 * Schematic Brush Reborn does by lower-casing its keys. Both sides are lower-cased by the database, so the case
 * mapping is the same for the stored and the requested name.
 */
public enum NameMatch {
    /**
     * Compares the lower-cased names byte by byte in utf8mb4. A plain {@code =} would use the column collation, which
     * on MySQL and MariaDB usually ignores accents as well.
     */
    MYSQL("CONVERT(LOWER(name) USING utf8mb4) COLLATE utf8mb4_bin = CONVERT(LOWER(?) USING utf8mb4) COLLATE utf8mb4_bin"),
    /**
     * Compares the lower-cased names. Text comparison in PostgreSQL is exact apart from case.
     */
    POSTGRES("LOWER(name) = LOWER(?)");

    private final String condition;

    NameMatch(String condition) {
        this.condition = condition;
    }

    /**
     * The condition, with one placeholder for the name.
     *
     * @return SQL condition
     */
    public String condition() {
        return condition;
    }
}
