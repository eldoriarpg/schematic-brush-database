---
sidebar_position: 2
---

# Setup

This page walks you through connecting Schematic Brush Reborn to a database.
It takes five steps.

## 1. Install the add-on

1. Make sure Schematic Brush Reborn 3 is installed and starts without errors.
2. Download the Schematic Brush Database jar from [Lyna](https://lyna.eldoria.de/products/9) and put it into your server's `plugins` folder.
3. Start the server once.

On the first start the add-on creates `plugins/SchematicBrushDatabase/config.yml`.
No database is switched on yet, so the console shows:

```text
No storage type active. Please enable a storage type.
```

That is expected. Stop the server and continue with the next step.

:::note
The database drivers are not inside the jar. Paper downloads them on startup as plugin libraries.
Your server needs internet access the first time it starts with the add-on.
:::

## 2. Prepare the database

Create an empty database and a user that may use it.
The add-on does not create the database itself.

It creates three tables in it: `brushes`, `presets` and `sbr_version`.
Use a database of its own, so these names do not clash with tables of other plugins.

For PostgreSQL you also pick a schema. The add-on creates the schema if it is missing and the user is allowed to create schemas.

## 3. Enter the connection details

Open `plugins/SchematicBrushDatabase/config.yml`.
Fill in the section for your database type and add the type to `activeTypes`.

This example connects to a MariaDB server:

```yaml
storages:
  activeTypes:
    - mariadb
  mariadb:
    host: localhost
    port: "3306"
    database: schematicbrush
    user: schematicbrush
    password: change-me
    connections: 3
```

The section names are `mariadb`, `mysql` and `postgres`.
The [configuration page](./configuration.md) explains every key and its default.

You can switch on more than one type at the same time. That is useful when you move data from one database to another.

## 4. Switch Schematic Brush Reborn to the database

Open `plugins/SchematicBrushReborn/config.yml` and set `storageType` in the `generalConfig` section:

```yaml
generalConfig:
  storageType: mariadb
```

Use exactly one of `mariadb`, `mysql` or `postgres`, written in lower case.
The default is `yaml`, the file storage of Schematic Brush Reborn.

Start the server. The console shows `Setting up storage for mariadb` (or your type) while the add-on connects.

:::tip Check the storage type
If the storage type is not registered, Schematic Brush Reborn falls back to the YAML storage.
It logs `Storage type <name> not registered. Using fallback YAML storage.` and lists the available types.
This happens when the type is missing from `activeTypes`, the spelling is different, or the connection failed.
:::

See [Configuration](/schematic-brush-reborn/configuration) for the other settings of the core plugin.

## 5. Move existing brushes and presets

Brushes and presets that are already saved stay in the old storage.
Copy them over with the migrate command of Schematic Brush Reborn:

```text
/sbra migrate <source> <target>
```

To copy everything from the YAML files into a MariaDB database:

```text
/sbra migrate yaml mariadb
```

- You need the permission `schematicbrush.admin.migrate`.
- Both storage types must be available. A database type is available once it is in `activeTypes` and connected.
- Entries in the target with the same name are overwritten, even if upper and lower case differ.
- Nothing is removed from the source.

See the [command reference](/schematic-brush-reborn/reference/commands) for the full list of core commands.

## Sharing brushes between servers

Do steps 1 to 4 on every server:

- Every server uses the same connection details.
- Every server has the same type in `activeTypes`.
- Every server uses the same `storageType` in Schematic Brush Reborn.

Run the migration from step 5 only once.

Brushes and presets are read from the database each time they are used, so changes show up on all servers.
The list of names used for tab completion is refreshed in the background. How often is set by `cacheRefreshSec` on the [configuration page](./configuration.md).

## Updating from version 1

1. Update Schematic Brush Reborn to version 3. Version 2 of this add-on does not run with older versions.
2. If your database was last used with a version older than 1.1, start it once with version 1.1.10 first.
   Version 2 cannot convert the old data format and stops with this message:

   ```text
   The database contains brushes or presets stored before version 1.1. Update it with SchematicBrushDatabase 1.1.10 before using version 2.
   ```

3. Replace the jar with version 2 and start the server.

The old `checkUpdates` switch in `config.yml` is read once and turned into the new `updates` section.
