import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    `java-library`
    kotlin("jvm") version "1.3.71"
    id("org.jetbrains.dokka") version "0.10.1"
}

repositories {
    jcenter()
    mavenCentral()
}

configurations {
    create("externalLibs")
}

dependencies {
    implementation(project(":MarioAI4J"))
    implementation(kotlin("stdlib"))

    implementation(group = "org.deeplearning4j", name = "deeplearning4j-core", version = "0.9.1")
    implementation(group = "org.slf4j", name = "slf4j-nop", version = "1.7.28")
    implementation(group = "org.nd4j", name = "nd4j-native-platform", version = "0.9.1")
    implementation(group = "org.datavec", name = "datavec-api", version = "0.9.1")
    implementation(group = "io.jenetics", name = "jenetics", version = "5.2.0")
    implementation(group = "org.knowm.xchart", name = "xchart", version = "3.6.3")
    implementation(files("$projectDir/lib/HuffmanCoding.jar"))

    testImplementation(group = "junit", name = "junit", version = "4.12")
    testImplementation(group = "io.mockk", name = "mockk", version="1.10.0")
}

tasks.withType<Test> {
    testLogging {
        events("PASSED", "FAILED", "SKIPPED")
    }

    this.testLogging {
        this.showStandardStreams = true
    }
}

val dokka by tasks.getting(DokkaTask::class) {
    outputFormat = "html"
    outputDirectory = "$buildDir/dokka"

    configuration {
        includes = listOf("docs/packages.md")
        includeNonPublic = false
        reportUndocumented = true
    }
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "cz.cuni.mff.aspect.launch.CoEvolveMultiKt"
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}

val compileKotlin by tasks.getting(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class) {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
