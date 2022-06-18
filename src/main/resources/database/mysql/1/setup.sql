CREATE TABLE presets
(
    uuid   BINARY(16)                   NOT NULL,
    name   TEXT                     NOT NULL,
    preset JSON NOT NULL,
    PRIMARY KEY (uuid(16), name(64)),
    CONSTRAINT preset
        CHECK (JSON_VALID(`preset`))
);

CREATE TABLE brushes
(
    uuid   BINARY(16)                   NOT NULL,
    name   TEXT                     NOT NULL,
    brush JSON NOT NULL,
    PRIMARY KEY (uuid(16), name(64)),
    CONSTRAINT brush
        CHECK (JSON_VALID(`brush`))
);
