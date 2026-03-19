SET SESSION sql_mode = REPLACE(@@sql_mode, 'STRICT_TRANS_TABLES', '');
SET SESSION sql_mode = REPLACE(@@sql_mode, 'STRICT_ALL_TABLES', '');
UPDATE brushes SET uuid = SUBSTR(uuid, 1, 16);
ALTER TABLE brushes MODIFY uuid BINARY(16) NOT NULL;
UPDATE presets SET uuid = SUBSTR(uuid, 1, 16);
ALTER TABLE presets MODIFY uuid BINARY(16) NOT NULL;
