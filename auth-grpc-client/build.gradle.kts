plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
    jcenter()
    maven { setUrl("https://kotlin.bintray.com/ktor") }
    maven { setUrl("https://kotlin.bintray.com/kotlin-js-wrappers") }
    maven { setUrl("https://jitpack.io") }
}

val coroutinesVersion: String by project
val kotlinVersion: String by project

dependencies {
    implementation(project(":auth-grpc-protocol"))
    implementation(kotlin("stdlib-jdk8", kotlinVersion))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
}

tasks {
    "compileKotlin" {
        dependsOn(project(":auth-grpc-protocol").tasks["generateProto"])
    }
}
