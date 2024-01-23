rootProject.name = "schematic-brush-database"

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
            version("sadu", "1.4.1")

            library("sadu-core","de.chojo.sadu", "sadu-core").versionRef("sadu")
            library("sadu-queries","de.chojo.sadu", "sadu-queries").versionRef("sadu")
            library("sadu-datasource","de.chojo.sadu", "sadu-datasource").versionRef("sadu")
            library("sadu-updater","de.chojo.sadu", "sadu-updater").versionRef("sadu")
            library("sadu-postgresql","de.chojo.sadu", "sadu-postgresql").versionRef("sadu")
            library("sadu-mariadb","de.chojo.sadu", "sadu-mariadb").versionRef("sadu")
            library("sadu-mysql","de.chojo.sadu", "sadu-mysql").versionRef("sadu")
            bundle("sadu", listOf("sadu-core","sadu-queries", "sadu-datasource", "sadu-updater","sadu-postgresql","sadu-mariadb","sadu-mysql"))
        }
    }
}
