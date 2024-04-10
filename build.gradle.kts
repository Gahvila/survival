plugins {
    java
    `maven-publish`
    id("io.github.goooler.shadow") version "8.1.7"
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
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly ("me.clip:placeholderapi:2.11.5")
    compileOnly ("net.luckperms:api:5.4")
    compileOnly("de.hexaoxi:carbonchat-api:3.0.0-beta.26")
    compileOnly ("com.github.koca2000:NoteBlockAPI:1.6.2")
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    implementation ("com.github.stefvanschie.inventoryframework:IF:0.10.14-SNAPSHOT")
    implementation ("com.github.simplix-softworks:simplixstorage:3.2.7")

    //commandapi
    implementation("dev.jorel:commandapi-bukkit-shade:9.3.0")
    compileOnly("dev.jorel:commandapi-annotations:9.3.0")
    annotationProcessor("dev.jorel:commandapi-annotations:9.3.0")
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
        dependencies {
            include(dependency("dev.jorel:commandapi-bukkit-shade:9.3.0"))
            include(dependency("com.github.simplix-softworks:simplixstorage:3.2.7"))
            include(dependency("com.github.stefvanschie.inventoryframework:IF:0.10.14-SNAPSHOT"))

        }
        relocate("dev.jorel.commandapi", "net.gahvila.selviytymisharpake.shaded.commandapi")
        relocate("de.leonhard.storage", "net.gahvila.selviytymisharpake.shaded.storage")
        relocate ("com.github.stefvanschie.inventoryframework", "net.gahvila.selviytymisharpake.shaded.inventoryframework")
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}
