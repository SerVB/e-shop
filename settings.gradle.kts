pluginManagement {
    val kotlinVersion: String by settings

    repositories {
        mavenCentral()

        maven { setUrl("https://plugins.gradle.org/m2/") }
    }

    plugins {
        kotlin("jvm") version kotlinVersion apply false
        kotlin("multiplatform") version kotlinVersion apply false
    }
}

rootProject.name = "e-shop"

include("auth")
include("product")
include("server-util")
include("server-util-test")
