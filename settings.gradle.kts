rootProject.name = "schematic-brush-database"
include("core")
include("legacy")
include("latest")

pluginManagement{
    repositories{
        mavenLocal()
        gradlePluginPortal()
        maven{
            name = "EldoNexus"
            url = uri("https://eldonexus.de/repository/maven-public/")
        }
    }
}

dependencyResolutionManagement{
    versionCatalogs{
        create("libs"){
            version("sadu", "2.2.4")

            library("sadu-core","de.chojo.sadu", "sadu-core").versionRef("sadu")
            library("sadu-queries","de.chojo.sadu", "sadu-queries").versionRef("sadu")
            library("sadu-datasource","de.chojo.sadu", "sadu-datasource").versionRef("sadu")
            library("sadu-updater","de.chojo.sadu", "sadu-updater").versionRef("sadu")
            library("sadu-postgresql","de.chojo.sadu", "sadu-postgresql").versionRef("sadu")
            library("sadu-mariadb","de.chojo.sadu", "sadu-mariadb").versionRef("sadu")
            library("sadu-mysql","de.chojo.sadu", "sadu-mysql").versionRef("sadu")
            bundle("sadu", listOf("sadu-core","sadu-queries", "sadu-datasource", "sadu-updater","sadu-postgresql","sadu-mariadb","sadu-mysql"))

            plugin("publishdata", "de.chojo.publishdata").version("1.4.0")
            plugin("licenser", "org.cadixdev.licenser").version("0.6.1")
            plugin("shadow", "io.github.goooler.shadow").version("8.1.8")
            plugin("pluginyml", "net.minecrell.plugin-yml.bukkit").version("0.6.0")
        }
    }
}
