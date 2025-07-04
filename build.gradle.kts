plugins {
    java
    `maven-publish`
    id("com.gradleup.shadow") version "8.3.5"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://repo.codemc.org/repository/maven-public/") }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://repo.gahvila.net/snapshots/") }
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
    maven("https://repo.aikar.co/content/groups/aikar/")

}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.7-R0.1-SNAPSHOT")

    compileOnly("net.gahvila:gahvilacore:2.3-SNAPSHOT")
    compileOnly ("me.clip:placeholderapi:2.11.6")
    compileOnly ("net.luckperms:api:5.4")
    implementation("net.gahvila:inventoryframework:0.11.2-SNAPSHOT")
    implementation("com.github.simplix-softworks:simplixstorage:3.2.7")

    //commandapi
    implementation("dev.jorel:commandapi-bukkit-shade-mojang-mapped:10.1.1")
    compileOnly("dev.jorel:commandapi-annotations:10.1.1")
    annotationProcessor("dev.jorel:commandapi-annotations:10.1.1")
}

group = "net.gahvila"
version = "2.0-SNAPSHOT"
description = "survival"
java.sourceCompatibility = JavaVersion.VERSION_21

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
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveFileName.set("${rootProject.name}-${version}.jar")
        relocate("dev.jorel.commandapi", "net.gahvila.survival.shaded.commandapi")
        relocate("de.leonhard.storage", "net.gahvila.survival.shaded.storage")
        relocate ("com.github.stefvanschie.inventoryframework", "net.gahvila.survival.shaded.inventoryframework")
    }

    processResources {
        expand(project.properties)
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
}
