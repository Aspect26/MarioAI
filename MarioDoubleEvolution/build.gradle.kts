import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    kotlin("jvm") version "1.3.72"
    id("org.jetbrains.dokka") version "0.10.1"
    application
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
    implementation(group = "io.jenetics", name = "jenetics", version = "6.0.0")
    implementation(group = "org.knowm.xchart", name = "xchart", version = "3.6.3")
    implementation(files("$projectDir/lib/HuffmanCoding.jar"))

    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter", version = "5.6.2")
    testImplementation(group = "org.hamcrest", name = "hamcrest-library", version = "2.2")
    testImplementation(group = "io.mockk", name = "mockk", version="1.10.0")
}

tasks.withType<Test> {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = TestExceptionFormat.FULL
        showStandardStreams = true
    }

}

tasks.withType<DokkaTask> {
    outputFormat = "html"
    outputDirectory = "$buildDir/dokka"

    configuration {
        includes = listOf("docs/packages.md")
        includeNonPublic = false
        reportUndocumented = true
    }
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "cz.cuni.mff.aspect.launch.CoEvolveExperimentKt"
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}

tasks.register<JavaExec>("runCoevolution") {
    main = "cz.cuni.mff.aspect.launch.CoEvolveMultiKt"
    classpath = sourceSets.main.get().runtimeClasspath
}

tasks.register<JavaExec>("runExperiment") {
    main = "cz.cuni.mff.aspect.launch.CoEvolveExperimentKt"
    classpath = sourceSets.main.get().runtimeClasspath
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}
