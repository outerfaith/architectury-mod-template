plugins {
    `java-library`
    idea
    id("dev.architectury.loom") version "1.11-SNAPSHOT"
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("com.gradleup.shadow") version "9.1.0"
}

val minecraftVersion: String by project
val parchmentVersion: String by project
val fabricLoaderVersion: String by project
val architecturyVersion: String by project

architectury {
    minecraft = minecraftVersion
    common("fabric", "neoforge")
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    modApi("dev.architectury:architectury:$architecturyVersion") // FIXME - remove this if you don't want the architectury api
}

loom {
    accessWidenerPath = file("src/main/resources/modname.accesswidener")
}

allprojects {
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "architectury-plugin")
    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "idea")
    
    loom {
        silentMojangMappingsLicense()
    }
    
    repositories {
        maven("https://maven.parchmentmc.org") {
            content { includeGroup("org.parchmentmc.data") }
        }
    }

    dependencies {
        minecraft("com.mojang:minecraft:$minecraftVersion")
        mappings(loom.layered {
            officialMojangMappings()
            parchment("org.parchmentmc.data:parchment-$minecraftVersion:$parchmentVersion@zip")
        })
    }
    
    java {
        toolchain.languageVersion = JavaLanguageVersion.of(21)
        withSourcesJar()
    }
    
    idea.module.isDownloadSources = true
    
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}
