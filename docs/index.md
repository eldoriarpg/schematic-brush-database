---
sidebar_position: 1
---

# Schematic Brush Database

Schematic Brush Database is an add-on for [Schematic Brush Reborn](/schematic-brush-reborn).
It stores saved brushes and presets in a SQL database instead of files.

## Why use it

Schematic Brush Reborn keeps saved brushes and presets in YAML files on each server by default.
That works well for a single server.

If you run several servers, every server has its own files. A brush saved on one server is missing on the others.
With this add-on, all servers read and write the same database. Save a brush once and it is there on every server.

The add-on supports three databases:

- MariaDB
- MySQL
- PostgreSQL

It adds no commands of its own and does not change how brushes work. Players use brushes and presets exactly as before.

## Requirements

| What | Version |
|---|---|
| Schematic Brush Reborn | 3 |
| Paper | 1.21.11 or newer |
| Java | 25 |
| Database | MariaDB, MySQL or PostgreSQL |

Schematic Brush Reborn itself needs FastAsyncWorldEdit.
See [Getting started](/schematic-brush-reborn/getting-started) for the core plugin.

:::info Older versions
Version 2 of this add-on only works with Schematic Brush Reborn 3.
For Schematic Brush Reborn 2, use version 1.1.10.
:::

## How it works

1. The add-on connects to every database type you switch on in its config.
2. Each connected database becomes a storage type in Schematic Brush Reborn: `mariadb`, `mysql` or `postgres`.
3. You tell Schematic Brush Reborn which storage type to use.
4. Saved brushes and presets now live in that database.

The add-on creates its tables on the first connection and keeps them up to date on later starts.

Brush and preset names work as they do in the YAML storage: a name matches only exactly the same name, and upper and
lower case do not matter. `Tree` and `tree` are the same brush, so saving `tree` replaces a saved `Tree`.
`%` and `_` in a name are ordinary characters.

:::note
On MySQL and MariaDB, saving treats names that differ only in accents (`Baum` and `Bäum`), or only after their 64th
character, as the same name, so the new brush or preset replaces the saved one. Looking names up and removing them
still matches exactly.
:::

## Next steps

- [Set up the database](./setup.md): install the add-on, connect it and switch Schematic Brush Reborn over.
- [Configuration](./configuration.md): every setting in the add-on's config files.
