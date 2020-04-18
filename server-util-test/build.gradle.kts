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

val kotestVersion: String by project
val kotlinVersion: String by project
val ktorVersion: String by project
val jsonPathVersion: String by project

dependencies {
    implementation(kotlin("stdlib-jdk8", kotlinVersion))
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("io.ktor:ktor-server-tests:$ktorVersion")
    implementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    implementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    implementation("io.kotest:kotest-assertions-json:$kotestVersion")
    implementation("com.jayway.jsonpath:json-path:$jsonPathVersion")
}
