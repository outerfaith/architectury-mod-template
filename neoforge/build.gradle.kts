architectury {
    platformSetupLoomIde()
    neoForge()
}

loom {
    accessWidenerPath = project(":").loom.accessWidenerPath
}

val common: Configuration by configurations.creating
val shade: Configuration by configurations.creating
val developmentNeoForge: Configuration by configurations.getting

configurations {
    getByName("compileClasspath").extendsFrom(common)
    getByName("runtimeClasspath").extendsFrom(common)
    developmentNeoForge.extendsFrom(common)
}

val neoforgeVersion: String by rootProject
val architecturyVersion: String by rootProject

repositories {
    maven("https://maven.neoforged.net/releases/")
}

dependencies {
    neoForge("net.neoforged:neoforge:$neoforgeVersion")
    modApi("dev.architectury:architectury-neoforge:$architecturyVersion") // FIXME - remove this if you don't want the architectury api

    common(project(":", "namedElements")) { isTransitive = false }
    shade(project(":", "transformProductionNeoForge")) { isTransitive = false }
}

tasks {
    processResources {
        inputs.property("version", project.version)
        inputs.property("modName", rootProject.name)
        inputs.property("modId", "${rootProject.name.lowercase()}-platform-neoforge")
        inputs.property("minecraftVersion", rootProject.property("minecraftVersion"))
        inputs.property("architecturyVersion", architecturyVersion)

        filesMatching("neoforge.mods.toml") {
            expand(inputs.properties)
        }
    }

    shadowJar {
        configurations = listOf(shade)
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.flatMap { it.archiveFile })
    }
}
