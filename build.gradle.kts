plugins {
    id("fabric-loom") version "1.9-SNAPSHOT" // FIXME - you may want to update this if there's a newer version
    idea
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21) // FIXME - you will probably have to change this if not targeting 1.21.4
    withSourcesJar()
}

idea.module.isDownloadSources = true

repositories {
    maven("https://maven.parchmentmc.org")
}

val minecraftVersion: String by project
val parchmentVersion: String by project

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-$minecraftVersion:$parchmentVersion@zip")
    })
    // FIXME - see https://fabricmc.net/develop/ for the recommended versions
    modImplementation("net.fabricmc:fabric-loader:0.16.9")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.114.1+$minecraftVersion")
    
    testImplementation("net.fabricmc:fabric-loader-junit:0.16.9")
}

tasks {
    test {
        useJUnitPlatform()
    }
    
    withType<JavaCompile> {
        options.release = 21
        options.encoding = "UTF-8"
    }
    
    processResources {
        inputs.property("id", project.name)
        inputs.property("version", project.version)
        inputs.property("minecraftVersion", minecraftVersion)
        
        filesMatching("fabric.mod.json") {
            expand(inputs.properties)
        }
    }
}

loom {
    accessWidenerPath = project.file("src/main/resources/mod.accesswidener")
    
    // <editor-fold desc="Vineflower config">
    decompilers {
        getByName("vineflower") {
            options.put("mark-corresponding-synthetics", "1")
            options.put("synthetic-not-set", "1")
            options.put("ternary-constant-simplification", "1")
            options.put("include-runtime", "current")
            options.put("decompile-complex-constant-dynamic", "1")
            options.put("indent-string", "    ")
            options.put("decompile-inner", "1")
            options.put("remove-bridge", "1")
            options.put("decompile-generics", "1")
            options.put("ascii-strings", "0")
            options.put("remove-synthetic", "1")
            options.put("include-classpath", "1")
            options.put("inline-simple-lambdas", "1")
            options.put("ignore-invalid-bytecode", "0")
            options.put("bytecode-source-mapping", "1")
            options.put("dump-code-lines", "1")
        }
    }
    // </editor-fold>
}
