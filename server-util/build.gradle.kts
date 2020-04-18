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

val kotlinVersion: String by project
val ktorVersion: String by project

dependencies {
    implementation(kotlin("stdlib-jdk8", kotlinVersion))
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
}
