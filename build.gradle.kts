plugins {
    id("org.cadixdev.licenser") version "0.6.1"
    java
}

group = "de.eldoria.schematic-brush-database"
version = "1.1.3"

allprojects {
    apply {
        plugin<JavaPlugin>()
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
    }
}

license {
    header(rootProject.file("HEADER.txt"))
    include("**/*.java")
}
