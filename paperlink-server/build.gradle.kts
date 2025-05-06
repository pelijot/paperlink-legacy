plugins {
    id("java")
}

group = "network.tecnocraft"
version = "L1.0"
java.toolchain.languageVersion.set(JavaLanguageVersion.of(21)) // <-- explicitly set Java 21

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from(sourceSets.main.get().resources.srcDirs) {
            include("plugin.yml")
            filteringCharset = "UTF-8"
        }
    }
    jar {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        archiveBaseName.set("paperlink-server")
        archiveVersion.set("1.0.8")
    }
}