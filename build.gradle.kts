plugins {
    id("org.cadixdev.licenser") version "0.6.1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("de.chojo.publishdata") version "1.0.4"
    id("de.nycode.spigot-dependency-loader") version "1.1.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    java
    `maven-publish`
}

group = "de.eldoria"
version = "1.0.0"
val shadebase = "de.eldoria." + rootProject.name + ".libs."

repositories {
    mavenCentral()
    maven("https://eldonexus.de/repository/maven-public/")
    maven("https://eldonexus.de/repository/maven-proxies/")
}

dependencies {
    implementation("de.chojo", "sql-util", "1.4.4"){
        exclude("com.zaxxer")
    }

    compileOnly("de.eldoria", "schematicbrushreborn-api", "2.1.9-SNAPSHOT")
    compileOnly("org.spigotmc", "spigot-api", "1.16.5-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldedit", "worldedit-bukkit", "7.2.10")

    spigot("org.postgresql", "postgresql", "42.4.0")
    spigot("com.fasterxml.jackson.core", "jackson-databind", "2.0.1")
    spigot("org.mariadb.jdbc", "mariadb-java-client", "3.0.5")
    spigot("mysql", "mysql-connector-java", "8.0.29")
    spigot("com.zaxxer", "HikariCP", "5.0.1")

    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.8.2")
    testImplementation("org.spigotmc", "spigot-api", "1.16.5-R0.1-SNAPSHOT")
    testImplementation("de.eldoria", "eldo-util", "1.13.5")
    testImplementation("com.fasterxml.jackson.core", "jackson-databind", "2.0.1")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine")
}

license {
    header(rootProject.file("HEADER.txt"))
    include("**/*.java")
}

java {
    withSourcesJar()
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_17
}

publishData {
    useEldoNexusRepos()
    publishComponent("java")
}

publishing {
    publications.create<MavenPublication>("maven") {
        publishData.configurePublication(this)
    }

    repositories {
        maven {
            authentication {
                credentials(PasswordCredentials::class) {
                    username = System.getenv("NEXUS_USERNAME")
                    password = System.getenv("NEXUS_PASSWORD")
                }
            }

            setUrl(publishData.getRepository())
            name = "EldoNexus"
        }
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    compileTestJava {
        options.encoding = "UTF-8"
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    shadowJar {
        relocate("de.eldoria.eldoutilities", "de.eldoria.schematicbrush.libs.eldoutilities")
        relocate("de.eldoria.messageblocker", "de.eldoria.schematicbrush.libs.messageblocker")
        relocate("net.kyori", "de.eldoria.schematicbrush.libs.kyori")
        mergeServiceFiles()
    }

    processResources {
        from(sourceSets.main.get().resources.srcDirs) {
            filesMatching("plugin.yml") {
                expand(
                    "version" to publishData.getVersion(true)
                )
            }
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }

    register<Copy>("copyToServer") {
        val path = project.property("targetDir") ?: "";
        if (path.toString().isEmpty()) {
            println("targetDir is not set in gradle properties")
            return@register
        }
        println("Copying jar to $path")
        from(shadowJar)
        destinationDir = File(path.toString())
    }

    build {
        dependsOn(shadowJar)
    }
}

bukkit{
    main = "de.eldoria.sbrdatabase.SbrDatabase"
    apiVersion = "1.16"
    authors = listOf("RainbowDashLabs")
    depend = listOf("SchematicBrushReborn")
}
