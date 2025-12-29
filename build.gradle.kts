plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.serialization") version "2.3.0"
    id("com.gradleup.shadow") version "8.3.6"
    application
}

group = "com.rentamap"
version = "1.0.0"

dependencies {
    // CLI framework
    implementation("com.github.ajalt.clikt:clikt:4.2.2")

    // JSON serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    // Kotlin stdlib
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.0")

    // Testing
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("io.kotest:kotest-property:5.8.0")
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("com.rentamap.cli.MainKt")
}

tasks.shadowJar {
    archiveBaseName.set("french-property-investment")
    archiveClassifier.set("")
    archiveVersion.set("")
    manifest {
        attributes["Main-Class"] = "com.rentamap.cli.MainKt"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
