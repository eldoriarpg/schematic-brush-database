plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.publishdata)
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
    shadowJar {
        relocate("de.eldoria.eldoutilities", "de.eldoria.schematicbrush.libs.eldoutilities")
        relocate("de.eldoria.messageblocker", "de.eldoria.schematicbrush.libs.messageblocker")
        mergeServiceFiles()
    }

}
