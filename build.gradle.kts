import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.20"
    application
}

group = "me.alekseinovikov"
version = "1.0-SNAPSHOT"

val akkaVersion: String by project
val logBackVersion: String by project

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.typesafe.akka:akka-actor-typed_2.13:${akkaVersion}")
    implementation("ch.qos.logback:logback-classic:${logBackVersion}")

    testImplementation(kotlin("test"))
    testImplementation("com.typesafe.akka:akka-actor-testkit-typed_2.13:${akkaVersion}")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("MainKt")
}