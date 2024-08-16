plugins {
    id("java")
    alias(libs.plugins.pluginyml)
    alias(libs.plugins.publishdata)
}

publishData {
    useEldoNexusRepos()
}

dependencies {
    bukkitLibrary(libs.bundles.sadu)
    bukkitLibrary("org.postgresql", "postgresql", "42.7.3")
    bukkitLibrary("org.mariadb.jdbc", "mariadb-java-client", "3.4.1")
    bukkitLibrary("mysql", "mysql-connector-java", "8.0.33")
    bukkitLibrary("com.zaxxer", "HikariCP", "5.1.0")

    compileOnly("de.eldoria", "schematicbrushreborn-api", "2.7.1")
    compileOnly("org.spigotmc", "spigot-api", "1.16.5-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldedit", "worldedit-bukkit", "7.2.18")


    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.10.1")
    testImplementation("de.eldoria", "schematicbrushreborn-api", "2.7.1")
    testImplementation("org.spigotmc", "spigot-api", "1.16.5-R0.1-SNAPSHOT")
    testImplementation("com.fasterxml.jackson.core", "jackson-databind", "2.17.2")

    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine")
}

bukkit {
    name = "SchematicBrushDatabase"
    main = "de.eldoria.sbrdatabase.SbrDatabase"
    apiVersion = "1.16"
    version = publishData.getVersion(true)
    authors = listOf("RainbowDashLabs")
    depend = listOf("SchematicBrushReborn")
}
