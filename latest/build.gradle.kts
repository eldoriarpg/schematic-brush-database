plugins {
    alias(libs.plugins.publishdata)
    alias(libs.plugins.shadow)
    `maven-publish`
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
    register<Copy>("copyToServer") {
        val path = rootProject.property("targetDir") ?: "";
        if (path.toString().isEmpty()) {
            println("targetDir is not set in gradle properties")
            return@register
        }
        println("Copying jar to $path")
        from(shadowJar)
        into(path.toString())
        rename{"schematic-brush-database.jar"}
    }
    shadowJar {
        relocate("de.eldoria.messageblocker", "de.eldoria.schematicbrush.libs.messageblocker")
        mergeServiceFiles()
    }
}
