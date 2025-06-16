plugins {
    kotlin("jvm") version "2.1.10"
    application
    kotlin("plugin.serialization") version "2.1.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val javafxVersion = "24.0.1"

val os = System.getProperty("os.name").lowercase()
val platform = when {
    os.contains("win") -> "win"
    os.contains("mac") -> "mac"
    os.contains("linux") -> "linux"
    else -> throw GradleException("Unsupported OS: $os")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.openjfx:javafx-base:$javafxVersion:$platform")
    implementation("org.openjfx:javafx-controls:$javafxVersion:$platform")
    implementation("org.openjfx:javafx-graphics:$javafxVersion:$platform")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
}

application {
    mainClass.set("MainKt")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(23)
}