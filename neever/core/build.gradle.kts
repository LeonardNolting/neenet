import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    application
}

group = "net.nee"
version = "1.0-SNAPSHOT"

val ktorVersion = "1.5.1"

repositories {
    jcenter()
    mavenCentral()
    maven(url = "https://dl.bintray.com/kotlin/kotlinx")
    maven(url = "https://dl.bintray.com/kotlin/ktor")
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation(project(":neever"))
    implementation(project(":shared"))
    api(project(":units"))

    // Server
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-network:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.2.3")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")

    // Reflection
//    implementation("org.reflections:reflections:0.9.12")
    implementation("net.oneandone.reflections8:reflections8:0.11.7")
    implementation(kotlin("reflect"))

    // Logging
    implementation("io.github.microutils:kotlin-logging:1.12.0")

    // HTTP Client
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-gson:$ktorVersion")

    // NBT parsing/manipulation/saving
    api("com.github.jglrxavpok:Hephaistos:1.1.7")
    api("com.github.jglrxavpok:Hephaistos:1.1.7:gson")
    api("com.github.jglrxavpok:Hephaistos:1.1.7") {
        capabilities {
            requireCapability("org.jglrxavpok.nbt:Hephaistos-gson")
        }
    }
    /*api("com.github.DevSrSouza:KotlinNBT:master-SNAPSHOT")
	api("org.jetbrains.kotlinx:kotlinx-io-jvm:0.1.16")*/

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "13"
        freeCompilerArgs = listOf(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xopt-in=kotlin.ExperimentalUnsignedTypes",
            "-Xopt-in=kotlin.ExperimentalStdlibApi"
        )
        useIR = true
        languageVersion = "1.5"
        apiVersion = "1.5"
    }
}

application {
    mainClassName = "ServerKt"
}