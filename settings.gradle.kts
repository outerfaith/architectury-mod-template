pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net")
        maven("https://maven.architectury.dev/")
        maven("https://files.minecraftforge.net/maven/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "architectury-mod-template" // FIXME - replace this with mod name
include(":neoforge", ":fabric")
