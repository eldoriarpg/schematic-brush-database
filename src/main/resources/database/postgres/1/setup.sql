CREATE TABLE sbr_database.presets
(
    uuid   bytea NOT NULL,
    name   TEXT  NOT NULL,
    preset json  NOT NULL,
    PRIMARY KEY (uuid, name)
);

CREATE TABLE sbr_database.brushes
(
    uuid  bytea NOT NULL,
    name  TEXT  NOT NULL,
    brush JSON  NOT NULL,
    PRIMARY KEY (uuid, name)
);
