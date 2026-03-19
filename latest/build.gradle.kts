plugins {
    alias(libs.plugins.publishdata)
    alias(libs.plugins.shadow)
    alias (libs.plugins.runserver)
    `maven-publish`
}

publishData {
    addBuildData()
    useEldoNexusRepos()
    publishTask("shadowJar")
    publishTask("javadocJar")
    publishTask("sourcesJar")
}

dependencies {
    implementation(project(":core"))
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
    runServer {
        minecraftVersion("1.21.8")
        downloadPlugins {
            url("https://ci.athion.net/job/FastAsyncWorldEdit/1175/artifact/artifacts/FastAsyncWorldEdit-Paper-2.13.3-SNAPSHOT-1175.jar")
            url("https://download.luckperms.net/1626/bukkit/loader/LuckPerms-Bukkit-5.5.38.jar")
        }

        jvmArgs("-Dcom.mojang.eula.agree=true")
    }

    shadowJar {
        val shadebase = "de.eldoria.schematicbrush.libs."
        relocate("de.eldoria.messageblocker", shadebase + "messageblocker")
        relocate("com.fasterxml", shadebase + "fasterxml")
        relocate("de.eldoria.jacksonbukkit", shadebase + "jacksonbukkit")
        relocate("de.eldoria.eldoutilities", shadebase + "utilities")
        mergeServiceFiles()
    }

    build {
        dependsOn(shadowJar)
    }
}
