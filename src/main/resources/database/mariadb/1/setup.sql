CREATE TABLE public.brushes
(
    uuid  BINARY(128) NOT NULL ,
    name  TEXT       NOT NULL,
    brush MEDIUMTEXT NOT NULL,
    CONSTRAINT `brushes_uuid_name(64)_uindex`
        UNIQUE (uuid, name(64))
);

CREATE TABLE public.presets
(
    uuid   BINARY(128) NOT NULL ,
    name   TEXT       NOT NULL,
    preset MEDIUMTEXT NOT NULL,
    CONSTRAINT `presets_uuid_name(64)_uindex`
        UNIQUE (uuid, name(64))
);
