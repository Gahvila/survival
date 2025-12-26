import java.util.Properties
import kotlin.apply
import kotlin.toString

plugins {
    java
    `maven-publish`
    id("com.gradleup.shadow") version "8.3.5"
}

val propsFile = file("gradle.properties")
val props = Properties().apply {
    load(propsFile.inputStream())
}

val versionPrefix = props["version"].toString()
val currentBuildNumber = props["buildNumber"].toString().toInt()
val newBuildNumber = currentBuildNumber + 1

val generatedVersion = "$versionPrefix+b$newBuildNumber"

version = generatedVersion

extra["generatedVersion"] = generatedVersion


group = "net.gahvila"
version = generatedVersion
description = "survival"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://repo.codemc.org/repository/maven-public/") }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://repo.gahvila.net/snapshots/") }
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://maven.enginehub.org/repo/")

}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")

    compileOnly("net.gahvila:gahvilacore:2.3-SNAPSHOT")
    compileOnly ("me.clip:placeholderapi:2.11.6")
    compileOnly ("net.luckperms:api:5.4")
    compileOnly("net.crashcraft:crashclaim:1.0.44")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9")
    implementation("net.gahvila:inventoryframework:0.11.2-SNAPSHOT")
    implementation("com.github.simplix-softworks:simplixstorage:3.2.7")

    //commandapi
    implementation("dev.jorel:commandapi-paper-shade:11.1.0")
    compileOnly("dev.jorel:commandapi-annotations:11.1.0")
    annotationProcessor("dev.jorel:commandapi-annotations:11.1.0")
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    assemble {
        dependsOn("updateBuildNumber")
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveFileName.set("${rootProject.name}-${version}.jar")
        relocate("dev.jorel.commandapi", "net.gahvila.survival.shaded.commandapi")
        relocate("de.leonhard.storage", "net.gahvila.survival.shaded.storage")
        relocate ("com.github.stefvanschie.inventoryframework", "net.gahvila.survival.shaded.inventoryframework")
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }


    register("updateBuildNumber") {
        doLast {
            props["buildNumber"] = newBuildNumber.toString()
            props.store(propsFile.outputStream(), null)
            println("Updated build number to $newBuildNumber")
        }
    }
    processResources {
        inputs.property("generatedVersion", generatedVersion) // make it cache-safe
        filesMatching("**/*.yml") { // or "**/*.properties", or all files if needed
            expand(
                mapOf(
                    "version" to generatedVersion,
                    "generatedVersion" to generatedVersion
                )
            )
        }
    }

}
