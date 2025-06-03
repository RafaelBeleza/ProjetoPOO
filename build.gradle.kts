plugins {
    kotlin("jvm") version "2.1.10"
    application
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
    implementation("org.jetbrains.exposed:exposed-core:0.43.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.43.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.43.0")
    implementation("mysql:mysql-connector-java:8.0.33")

    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    implementation("org.openjfx:javafx-base:$javafxVersion:$platform")
    implementation("org.openjfx:javafx-controls:$javafxVersion:$platform")
    implementation("org.openjfx:javafx-graphics:$javafxVersion:$platform")

    implementation("org.mindrot:jbcrypt:0.4")

    implementation("org.slf4j:slf4j-simple:2.0.9")
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