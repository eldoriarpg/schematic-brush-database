import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    java
    `maven-publish`
    alias(libs.plugins.spotless)
    alias(libs.plugins.publishdata)
    alias(libs.plugins.shadow)
    alias(libs.plugins.pluginyml.bukkit)
    alias(libs.plugins.runserver)
}

val shadebase = "de.eldoria.schematicbrush.libs."

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://eldonexus.de/repository/maven-public/")
    maven("https://eldonexus.de/repository/maven-proxies/")
}

dependencies {
    compileOnly(libs.paper)
    compileOnly(libs.jetbrains.annotations)
    compileOnly(libs.schematicbrushreborn.api)
    compileOnly(libs.bundles.utilities)
    compileOnly(platform(libs.jackson.bom))
    compileOnly(libs.bundles.jackson)

    bukkitLibrary(libs.bundles.sadu)
    bukkitLibrary(libs.bundles.drivers)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.paper)
    testImplementation(libs.schematicbrushreborn.api)
    testImplementation(libs.bundles.utilities)
    testImplementation(platform(libs.jackson.bom))
    testImplementation(libs.bundles.jackson)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
    withJavadocJar()
}

spotless {
    java {
        licenseHeaderFile(rootProject.file("HEADER.txt"))
        target("**/*.java")
    }
}

publishData {
    eldoNexus(dev = false)
    component = "shadow"
    buildData {
        put("lynaProduct", "9")
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    shadowJar {
        relocate("com.fasterxml", shadebase + "fasterxml")
        relocate("tools.jackson", shadebase + "jackson")
        relocate("dev.chojo.ocular", shadebase + "ocular")
        relocate("de.eldoria.jacksonbukkit", shadebase + "jacksonbukkit")
        relocate("de.eldoria.eldoutilities", shadebase + "utilities")
        mergeServiceFiles()
    }

    test {
        useJUnitPlatform()
    }

    build {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion("26.3")
        javaLauncher = project.javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(25)
        }
        downloadPlugins {
            modrinth("fastasyncworldedit", "2.16.1")
            modrinth("luckperms", "v5.5.71-bukkit")
            // TODO: download Schematic Brush Reborn 3 once it has a stable public download URL
        }

        jvmArgs("-Dcom.mojang.eula.agree=true")
    }
}

bukkit {
    name = "SchematicBrushDatabase"
    main = "de.eldoria.sbrdatabase.SbrDatabase"
    apiVersion = "1.21.11"
    version = project.version.toString()
    authors = listOf("RainbowDashLabs")
    depend = listOf("SchematicBrushReborn")

    permissions {
        register("schematicbrushdatabase.updates") {
            description = "Hear about new SchematicBrushDatabase versions on join and download them"
            default = BukkitPluginDescription.Permission.Default.OP
        }
    }
}
