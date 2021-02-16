import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
//    kotlin("multiplatform") version "1.4.30"
	kotlin("jvm") version "1.4.30"
	id("info.kunalsheth.units") version "6.1.0"
}

repositories.jcenter()

group = "net.nee"
version = "1.0-SNAPSHOT"

val generateUnits = tasks.named<info.kunalsheth.units.GenerateUnitsOfMeasureTask>("generateUnitsOfMeasure")

sourceSets["main"].withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
	kotlin.srcDir(generateUnits.get().generatedSrcDir)
}

generateUnits {
	// relationships += ...
	// quantities += ...
	// unitsOfMeasure += ...
}

dependencies { implementation(kotlin("stdlib-jdk8")) }

/*kotlin {
    jvm()
    sourceSets {
        *//*val commonMain by getting {
            kotlin.srcDir(generateUnitsOfMeasure.generatedSrcDir)
            dependencies { implementation kotlin ('stdlib-common') }
        }
        val jvm by getting {
            jvm { dependencies { implementation kotlin ('stdlib-jdk8') } }
        }*//*
    }
}*/

/*dependencies {
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
}*/

tasks.test {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	dependsOn(generateUnits)
	kotlinOptions.jvmTarget = "13"
}

//apply(from = "units.gradle.kts")