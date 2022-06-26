CREATE TABLE sbr_database.presets
(
    uuid   bytea,
    name   TEXT NOT NULL,
    preset json NOT NULL,
    PRIMARY KEY (uuid, name)
);

CREATE TABLE sbr_database.brushes
(
    uuid  bytea,
    name  TEXT NOT NULL,
    brush json NOT NULL,
    PRIMARY KEY (uuid, name)
);
