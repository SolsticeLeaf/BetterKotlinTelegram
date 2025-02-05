import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `maven-publish`
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.github.ben-manes.versions") version "0.51.0"
    id("org.owasp.dependencycheck") version "12.0.0"
    kotlin("jvm") version "2.0.21"
}

group = "solstice.telegram"
version = "2.2.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("org.tomlj:tomlj:1.1.1")
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("org.slf4j:slf4j-log4j12:2.0.16")
    implementation("org.mongodb:mongo-java-driver:3.12.14")

    implementation("org.telegram:telegrambots-longpolling:8.2.0")
    implementation("org.telegram:telegrambots-client:8.2.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
}

tasks.jar {
    manifest.attributes["Main-Class"] = "solstice.telegram.Main"
    manifest.attributes["Class-Path"] = configurations
        .runtimeClasspath
        .get()
        .joinToString(separator = " ") { file ->
            "libs/${file.name}"
        }
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

tasks.withType<ShadowJar> {
    manifest {
        attributes["Main-Class"] = "solstice.telegram.Main"
    }
}

tasks.named("build") {
    finalizedBy("copyJar")
}

tasks.register<Copy>("copyJar") {
    from(tasks.named("shadowJar"))
    into("$buildDir/libs")
    rename { "BetterKotlinTelegram-$version.jar" }
    finalizedBy("deleteJar")
}

tasks.register<Delete>("deleteJar") {
    delete(tasks.named("shadowJar"))
}

val groupIdStr = group.toString()
val versionStr = version.toString()
var artifactIdStr = rootProject.name

publishing {
    repositories {
        maven {
            name = "SolsticeLeafRepository"
            url = uri("https://repo.kiinse.dev/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = groupIdStr
            artifactId = artifactIdStr
            version = versionStr
            from(components["java"])
        }
    }
}
