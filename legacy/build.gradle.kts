plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.publishdata)
    `maven-publish`
}

dependencies {
    implementation(project(":core"))
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
    shadowJar {
        relocate("de.eldoria.eldoutilities", "de.eldoria.schematicbrush.libs.eldoutilities")
        mergeServiceFiles()
    }

    build {
        dependsOn(shadowJar)
    }
}
