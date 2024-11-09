plugins {
    kotlin("jvm") version "1.9.10" // Stable Kotlin version
    application // Add the application plugin
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral() // Use Maven Central for dependencies
}

dependencies {
    implementation("io.ktor:ktor-server-core:2.3.4")
    implementation("io.ktor:ktor-server-cors:2.3.4") // Add this for CORS
    implementation("io.ktor:ktor-server-status-pages:2.3.4") // For StatusPages
    implementation("io.ktor:ktor-server-netty:2.3.4") // For Netty server

    // Content negotiation and JSON serialization
    implementation("io.ktor:ktor-server-content-negotiation:2.3.4")
    implementation("io.ktor:ktor-serialization-gson:2.3.4") // Ensure versions match

    // Logging (SLF4J with Logback)
    implementation("ch.qos.logback:logback-classic:1.4.11")


//    // Ktor core and Netty server
//    implementation("io.ktor:ktor-server-core:2.3.3")
//    implementation("io.ktor:ktor-server-netty:2.3.3")
//
//    // Content negotiation and JSON serialization
//    implementation("io.ktor:ktor-server-content-negotiation:2.3.3")
//    implementation("io.ktor:ktor-serialization-gson:2.3.3")
//
//    // Logging (SLF4J with Logback)
//    implementation("ch.qos.logback:logback-classic:1.4.11")

    // Test dependencies
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("MainKt") // Ensure this matches your main Kotlin file
}

tasks.test {
    useJUnitPlatform() // Use JUnit for testing
}

kotlin {
    jvmToolchain(20) // Use JDK 20 for compatibility
}
