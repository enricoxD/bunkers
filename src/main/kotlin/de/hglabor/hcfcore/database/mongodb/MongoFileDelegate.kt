package de.hglabor.hcfcore.database.mongodb

import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.Manager
import net.axay.kspigot.languageextensions.kotlinextensions.createIfNotExists
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.nio.file.Paths
import kotlin.reflect.KProperty

class MongoFileDelegate {
    companion object {
        private val file: File by lazy {
            Paths.get(Manager.dataFolder.parentFile.toString(), "mongodb", "MongoDB.yml").toFile()
                .also { it.createIfNotExists() }
        }
        val yamlConfiguration by lazy { YamlConfiguration.loadConfiguration(file) }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = yamlConfiguration.getString(property.name) ?: "N/A"
}