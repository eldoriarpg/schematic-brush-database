import com.diffplug.gradle.spotless.SpotlessPlugin
import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin

plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.spotless)
    java
}

group = "de.eldoria.schematic-brush-database"
version = "1.1.6"

allprojects {
    apply {
        plugin<JavaPlugin>()
        plugin<SpotlessPlugin>()
        plugin<ShadowPlugin>()

    }

    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
        maven("https://eldonexus.de/repository/maven-public/")
        maven("https://eldonexus.de/repository/maven-proxies/")
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
        withSourcesJar()
        withJavadocJar()
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

            spotless {
        java {
            licenseHeaderFile(rootProject.file("HEADER.txt"))
            target("**/*.java")
        }
    }


        shadowJar {
            val shadebase = "de.eldoria.schematicbrush.libs."
            relocate("org.bstats", shadebase + "bstats")
            relocate("de.eldoria.messageblocker", shadebase + "messageblocker")
            relocate("com.fasterxml", shadebase + "fasterxml")
            relocate("de.eldoria.utilities", shadebase + "utilities")
            mergeServiceFiles()
            archiveVersion.set(rootProject.version as String)
        }
    }
}

