CREATE TABLE sbr_database.presets
(
    uuid   bytea NOT NULL,
    name   TEXT  NOT NULL,
    preset JSON  NOT NULL,
    PRIMARY KEY (uuid, name)
);
