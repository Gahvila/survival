/*
 * This file was generated by the Gradle 'init' task.
 *
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    java
    `maven-publish`
    id("io.github.goooler.shadow") version "8.1.7"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://repo.opencollab.dev/maven-snapshots/")
    }

    maven { url = uri("https://repo.codemc.org/repository/maven-public/") }

    maven {
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }

    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }

    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }

    maven {
        url = uri("https://jitpack.io")
    }

    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly ("me.clip:placeholderapi:2.10.10")
    compileOnly ("net.luckperms:api:5.4")
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    implementation ("com.github.simplix-softworks:simplixstorage:3.2.7")
    //implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}

group = "net.gahvila"
version = "1.20"
description = "SelviytymisHarpake"
java.sourceCompatibility = JavaVersion.VERSION_17

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}


tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    build {
        dependsOn(shadowJar)
    }
    assemble {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveFileName.set("${rootProject.name}-${version}.jar")
        relocate("de.leonhard.storage", "net.gahvila.selviytymisharpake.shaded.storage")
        //relocate("me.frap.vulcan.api", "net.gahvila.selviytymisharpake.shaded.vulcan.api")
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}
