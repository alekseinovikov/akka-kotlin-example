import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.20"
    application
}

group = "me.alekseinovikov"
version = "1.0-SNAPSHOT"

val akkaVersion: String by project
val logBackVersion: String by project
val akkaHttpVersion: String by project
val akkaStreamsVersion: String by project
val jacksonKotlinModule: String by project

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:${logBackVersion}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${jacksonKotlinModule}")

    implementation("com.typesafe.akka:akka-actor-typed_2.13:${akkaVersion}")
    implementation("com.typesafe.akka:akka-http_2.13:${akkaHttpVersion}")
    implementation("com.typesafe.akka:akka-http-jackson_2.13:${akkaHttpVersion}")
    implementation("com.typesafe.akka:akka-stream_2.13:${akkaStreamsVersion}")

    testImplementation(kotlin("test"))
    testImplementation("com.typesafe.akka:akka-actor-testkit-typed_2.13:${akkaVersion}")
    testImplementation("com.typesafe.akka:akka-http-testkit_2.13:${akkaHttpVersion}")
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