import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val javaVersion = 17
val kspigotVersion = "1.19.0"

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"

    id("io.papermc.paperweight.userdev") version "1.3.8"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
}

group = "de.hglabor"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // PaperMC Dependency
    paperDevBundle("1.19.2-R0.1-SNAPSHOT")

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.10")

    // KMongo
    compileOnly("org.litote.kmongo:kmongo-coroutine-serialization:4.8.0")

    // KSpigot dependency
    implementation("net.axay", "kspigot", kspigotVersion)
    implementation(kotlin("stdlib-jdk8"))
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
    shadowJar {
        fun reloc(pkg: String) = relocate(pkg, "de.hglabor.dependency.$pkg")
        reloc("net.axay")
    }
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "$javaVersion"
        }
    }
}

bukkit {
    name = "Bunkers"
    apiVersion = "1.19"
    authors = listOf("BestAuto")
    main = "$group.bunkers.Bunkers"
    //main = "$group.hcfcore.HCFCore"
    version = getVersion().toString()
}