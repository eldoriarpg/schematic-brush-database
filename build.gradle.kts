plugins {
    id("org.cadixdev.licenser") version "0.6.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("de.chojo.publishdata") version "1.4.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    java
    `maven-publish`
}

group = "de.eldoria"
version = "1.1.3"
val shadebase = "de.eldoria." + rootProject.name + ".libs."

repositories {
    mavenCentral()
    maven("https://eldonexus.de/repository/maven-public/")
    maven("https://eldonexus.de/repository/maven-proxies/")
}

dependencies {
    bukkitLibrary(libs.bundles.sadu)

    compileOnly("de.eldoria", "schematicbrushreborn-api", "2.6.0")
    compileOnly("org.spigotmc", "spigot-api", "1.16.5-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldedit", "worldedit-bukkit", "7.2.18")

    bukkitLibrary("org.postgresql", "postgresql", "42.7.1")
    bukkitLibrary("org.mariadb.jdbc", "mariadb-java-client", "3.3.2")
    bukkitLibrary("mysql", "mysql-connector-java", "8.0.33")
    bukkitLibrary("com.zaxxer", "HikariCP", "5.1.0")

    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.10.1")
    testImplementation("de.eldoria", "schematicbrushreborn-api", "2.6.0")
    testImplementation("org.spigotmc", "spigot-api", "1.16.5-R0.1-SNAPSHOT")
    testImplementation("com.fasterxml.jackson.core", "jackson-databind", "2.14.2")

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
    addBuildData()
    useEldoNexusRepos()
    publishTask("shadowJar")
    publishTask("javadocJar")
    publishTask("sourcesJar")
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
        mergeServiceFiles()
        archiveClassifier.set("")
        archiveBaseName.set("SchematicBrushDatabase")
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


bukkit {
    name = "SchematicBrushDatabase"
    main = "de.eldoria.sbrdatabase.SbrDatabase"
    apiVersion = "1.16"
    version = publishData.getVersion(true)
    authors = listOf("RainbowDashLabs")
    depend = listOf("SchematicBrushReborn")
}
