architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    accessWidenerPath = project(":").loom.accessWidenerPath
}

val common: Configuration by configurations.creating
val shade: Configuration by configurations.creating
val developmentFabric: Configuration by configurations.getting

configurations {
    getByName("compileClasspath").extendsFrom(common)
    getByName("runtimeClasspath").extendsFrom(common)
    developmentFabric.extendsFrom(common)
}

val fabricLoaderVersion: String by rootProject
val fabricApiVersion: String by rootProject
val architecturyVersion: String by rootProject

dependencies {
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    modApi("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion") // FIXME - remove this if you don't need the fabric api
    modApi("dev.architectury:architectury-fabric:$architecturyVersion") // FIXME - remove this if you don't want the architectury api
    
    testImplementation("net.fabricmc:fabric-loader-junit:$fabricLoaderVersion")
    
    common(project(":", "namedElements")) { isTransitive = false }
    shade(project(":", "transformProductionFabric")) { isTransitive = false }
}

tasks {
    processResources {
        inputs.property("version", project.version)
        inputs.property("modName", rootProject.name)
        inputs.property("modId", "${rootProject.name.lowercase()}-platform-fabric")
        inputs.property("minecraftVersion", rootProject.property("minecraftVersion"))
        inputs.property("fabricLoaderVersion", fabricLoaderVersion)
        inputs.property("fabricApiVersion", fabricApiVersion)
        inputs.property("architecturyVersion", architecturyVersion)
        
        filesMatching("fabric.mod.json") {
            expand(inputs.properties)
        }
    }
    
    shadowJar {
        configurations = listOf(shade)
        archiveClassifier.set("dev-shadow")
    }
    
    remapJar {
        dependsOn(shadowJar)
        injectAccessWidener.set(true)
        inputFile.set(shadowJar.flatMap { it.archiveFile })
    }
}
