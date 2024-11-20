import net.neoforged.moddevgradle.dsl.RunModel

plugins {
    id("java-library")
    id("maven-publish")
    id("net.neoforged.moddev") version("1.0.21")
}

val mod_version: String by project
val mod_group_id: String by project
val mod_id: String by project

group = mod_group_id
version = mod_version

repositories {
    mavenCentral()
}

base {
    archivesName = mod_id
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

neoForge {
    version = project.property("neo_version") as String

    parchment {
        mappingsVersion = project.property("parchment_mappings_version") as String
        minecraftVersion = project.property("parchment_minecraft_version") as String
    }

    runs {
        val client: RunModel by creating {
            client()

            systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
        }

        val serer: RunModel by creating {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
        }

        val gameTestServer: RunModel by creating {
            type = "gameTestServer"
            systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
        }

        val data: RunModel by creating {
            data()

            programArguments.addAll("--mod", mod_id, "--all", "--output", file("src/generated/resources").absolutePath, "--existing", file("src/main/resources").absolutePath)
        }
        
        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")

            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        create(mod_id) {
            sourceSet(sourceSets.main.get())
        }
    }
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")

}

val generateModMetadata = tasks.register("generateModMetadata", ProcessResources::class) {
    val replacement = mapOf(
        "minecraft_version" to project.property("minecraft_version"),
        "minecraft_version_range" to project.property("minecraft_version_range"),
        "neo_version" to project.property("neo_version"),
        "neo_version_range" to project.property("neo_version_range"),
        "loader_version_range" to project.property("loader_version_range"),
        "mod_id" to mod_id,
        "mod_name" to project.property("mod_name"),
        "mod_license" to project.property("mod_license"),
        "mod_version" to project.property("mod_version"),
        "mod_authors" to project.property("mod_authors"),
        "mod_description" to project.property("mod_description"),
    )

    expand(replacement)
    from("src/main/templates")
    into("build/generated/sources/modMetadata")
}

sourceSets {
    main {
        resources {
            srcDir("src/generated/resources")
            srcDir(generateModMetadata)
        }
    }
}

neoForge.ideSyncTask(generateModMetadata)

val localRuntime: Configuration by configurations.creating

configurations {
    runtimeClasspath.get().extendsFrom(localRuntime)
}

tasks.withType(JavaCompile::class).configureEach {
    options.encoding = "UTF-8"
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}