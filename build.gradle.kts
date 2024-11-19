plugins {
    java
    `maven-publish`
    id("com.gradleup.shadow") version "8.3.5"
    id("io.papermc.paperweight.userdev") version "1.7.5"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://repo.codemc.org/repository/maven-public/") }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
}

dependencies {
    paperweight.paperDevBundle("1.21.3-R0.1-SNAPSHOT")

    compileOnly("net.gahvila:GahvilaCore:2.0")
    compileOnly ("me.clip:placeholderapi:2.11.6")
    compileOnly ("net.luckperms:api:5.4")
    compileOnly("de.hexaoxi:carbonchat-api:3.0.0-beta.27")
    compileOnly ("com.github.koca2000:NoteBlockAPI:1.6.2")
    implementation ("com.github.stefvanschie.inventoryframework:IF:0.10.18")
    implementation("com.github.simplix-softworks:simplixstorage:3.2.7")
    compileOnly ("net.crashcraft:CrashClaim:1.0.42")

    //commandapi
    implementation("dev.jorel:commandapi-bukkit-shade:9.6.1")
    compileOnly("dev.jorel:commandapi-annotations:9.6.1")
    annotationProcessor("dev.jorel:commandapi-annotations:9.6.1")
}

group = "net.gahvila"
version = "1.20"
description = "survival"
java.sourceCompatibility = JavaVersion.VERSION_21
paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.REOBF_PRODUCTION

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
        dependsOn(reobfJar)
    }
    shadowJar {

        relocate("dev.jorel.commandapi", "net.gahvila.survival.shaded.commandapi")
        relocate("de.leonhard.storage", "net.gahvila.survival.shaded.storage")
        relocate ("com.github.stefvanschie.inventoryframework", "net.gahvila.survival.shaded.inventoryframework")
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
}
