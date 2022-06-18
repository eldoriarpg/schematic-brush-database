CREATE TABLE presets
(
    uuid   BINARY(16)                   NOT NULL,
    name   TEXT                         NOT NULL,
    preset LONGTEXT COLLATE utf8mb4_bin NOT NULL,
    PRIMARY KEY (uuid, name(64)),
    CONSTRAINT preset
        CHECK (JSON_VALID(`preset`))
);

CREATE TABLE brushes
(
    uuid  BINARY(16)                   NOT NULL,
    name  TEXT                         NOT NULL,
    brush LONGTEXT COLLATE utf8mb4_bin NOT NULL,
    PRIMARY KEY (uuid, name(64)),
    CONSTRAINT brush
        CHECK (JSON_VALID(`brush`))
);
