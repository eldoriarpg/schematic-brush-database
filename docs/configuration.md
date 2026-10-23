---
sidebar_position: 3
---

# Configuration

The add-on keeps its settings in `plugins/SchematicBrushDatabase/`.
It writes both files with default values on the first start.

| File | What it holds |
|---|---|
| `config.yml` | Database connections, cache and update settings |
| `base_configuration.yml` | Log level and version bookkeeping |

The add-on reads its config only on startup and writes it again when it shuts down.
To change a setting, stop the server, edit the file, then start the server again.

## config.yml

This is the file with all defaults. Your copy may put quotes around text values; both forms work.

```yaml
updates:
  notice: true
  autoUpdate: false
  askForAutoUpdate: true
storages:
  activeTypes: []
  mysql:
    host: localhost
    port: "3306"
    database: public
    user: root
    password: passy
    connections: 3
  mariadb:
    host: localhost
    port: "3306"
    database: public
    user: root
    password: passy
    connections: 3
  postgres:
    host: localhost
    port: "5432"
    database: public
    user: root
    password: passy
    connections: 3
    schema: public
cache:
  cacheRefreshSec: 30
```

:::warning
The default user and password are placeholders. Change them to the user you created for the add-on.
:::

### storages

| Key | Default | What it does |
|---|---|---|
| `activeTypes` | empty | The database types to connect to. Allowed values: `mariadb`, `mysql`, `postgres`. Upper and lower case both work here. |
| `mysql` | see below | Connection to a MySQL server. |
| `mariadb` | see below | Connection to a MariaDB server. |
| `postgres` | see below | Connection to a PostgreSQL server. |

The add-on only connects to the types listed in `activeTypes`. The other sections are ignored.
If a listed database cannot be reached, the add-on fails to load and logs the error.
If Schematic Brush Reborn was set to use that database, it falls back to its YAML storage.

Each connection section has these keys:

| Key | Default | What it does |
|---|---|---|
| `host` | `localhost` | Address of the database server. |
| `port` | `"3306"`, for `postgres` `"5432"` | Port of the database server. |
| `database` | `public` | Name of the database. It must already exist. |
| `user` | `root` | User to log in with. |
| `password` | `passy` | Password of that user. |
| `connections` | `3` | Most connections the add-on keeps open to this database at the same time. |

The `postgres` section has one more key:

| Key | Default | What it does |
|---|---|---|
| `schema` | `public` | Schema that holds the tables. The add-on creates it if it is missing and the user may create schemas. |

### cache

| Key | Default | What it does |
|---|---|---|
| `cacheRefreshSec` | `30` | How old, in seconds, the list of brush and preset names may get before it is loaded again. This list is used for tab completion. |

Brushes and presets themselves are always read fresh from the database.
A lower value shows new names from other servers sooner, but asks the database more often.

### updates

The add-on looks for new versions every six hours.

| Key | Default | What it does |
|---|---|---|
| `notice` | `true` | Tell the console and admins when a new version is out. |
| `autoUpdate` | `false` | Download new stable versions on its own. They take effect on the next start. A new major version is never downloaded on its own. |
| `askForAutoUpdate` | `true` | Ask admins in chat whether to switch `autoUpdate` on. |

When both `notice` and `autoUpdate` are `false`, the add-on does not look for new versions at all.

Admins with the permission `schematicbrushdatabase.updates` see the notice when they join.
The permission is given to operators by default.
The notice has buttons to download the update or to switch on automatic updates. They run this command:

```text
/schematicbrushdatabase:eldo-update <download|enable-auto|later|never>
```

The command is only there once a new version was found.
The console, operators and players with `schematicbrushdatabase.updates` may use it.
Choosing "enable-auto" sets `autoUpdate` to `true`. Choosing "never" sets `askForAutoUpdate` to `false`.

Older versions used a single `checkUpdates` switch. If it is still in the file, it is read once as the value of `notice` and then removed.

## base_configuration.yml

| Key | Default | What it does |
|---|---|---|
| `logLevel` | `INFO` | How much the add-on writes to the console. |
| `version` | set by the add-on | Version of the config layout. Do not change it. |
| `lastInstalledVersion` | set by the add-on | Add-on version that wrote the file. Do not change it. |
