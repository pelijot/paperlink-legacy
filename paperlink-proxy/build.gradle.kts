plugins {
    id("java")
}

group = "network.tecnocraft"
version = "L1.0"
java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.velocitypowered.com/snapshots/")
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    jar {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        archiveBaseName.set("paperlink-proxy")
        archiveVersion.set("1.0.8")
    }
}