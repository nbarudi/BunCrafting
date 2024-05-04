plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.6.3"
    id("xyz.jpenilla.run-paper") version "2.2.4" // Adds runServer and runMojangMappedServer tasks for testing
}
group = "ca.bungo"
version = "1.0.0-SNAPSHOT"
description = "Plugin to allow for custom crafting up to a 5x5 grid"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

dependencies {
    paperweight.paperDevBundle("1.20.6-R0.1-SNAPSHOT")

    implementation("systems.manifold:manifold-ext:2024.1.13")
    annotationProcessor(group = "systems.manifold", name = "manifold-ext", version = "2024.1.13")
}

tasks {
    compileJava {
        options.release = 21
        options.compilerArgs.add("-Xplugin:Manifold")
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
}