CREATE TABLE sbr_database.presets
(
    uuid   bytea NOT NULL,
    name   TEXT NOT NULL,
    preset TEXT NOT NULL
);

CREATE UNIQUE INDEX presets_uuid_name_uindex
    ON presets (uuid, name);

CREATE TABLE sbr_database.brushes
(
    uuid  bytea NOT NULL,
    name  TEXT NOT NULL,
    brush TEXT NOT NULL
);

CREATE UNIQUE INDEX brushes_uuid_name_uindex
    ON brushes (uuid, name);
