pluginManagement {
    val kotlinVersion: String by settings
    val protobufGradlePluginVersion: String by settings

    repositories {
        mavenCentral()

        maven { setUrl("https://plugins.gradle.org/m2/") }
    }

    plugins {
        kotlin("jvm") version kotlinVersion apply false
        kotlin("multiplatform") version kotlinVersion apply false
        id("com.google.protobuf") version protobufGradlePluginVersion apply false
    }
}

rootProject.name = "e-shop"

include("auth")
include("auth-grpc-client")
include("auth-grpc-protocol")
include("product")
include("server-util")
include("server-util-test")
