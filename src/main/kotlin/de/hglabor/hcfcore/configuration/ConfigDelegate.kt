package de.hglabor.hcfcore.configuration

import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.Manager
import net.axay.kspigot.languageextensions.kotlinextensions.createIfNotExists
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.nio.file.Paths
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class ConfigDelegate<T : Any>(private val defaultValue: T, private val root: String, private val comment: String? = null) {
    companion object {
        val file: File by lazy {
            Paths.get(Manager.dataFolder.toString(), "config.yml").toFile().also { it.createIfNotExists() }
        }
        val yamlConfiguration: YamlConfiguration by lazy { YamlConfiguration.loadConfiguration(file) }
    }

    var value: T? = null

    operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        val key = "$root.${property.name}"
        val currentValue = value
        if (currentValue != null) return currentValue

        val newValue = try {
            (yamlConfiguration.getString(key) ?: "") toType defaultValue::class
        } catch (exc: Exception) {
            yamlConfiguration.set(key, defaultValue)
            yamlConfiguration.save(file)
            defaultValue
        }
        return newValue.also { value = it }
    }

    operator fun provideDelegate(thisRef: Any, property: KProperty<*>): ReadOnlyProperty<Any, T> {
        val key = "$root.${property.name}"
        if (!yamlConfiguration.contains(key)) {
            yamlConfiguration.set(key, defaultValue)
            if (comment != null) {
                yamlConfiguration.setComments(key, listOf(comment))
            }
            yamlConfiguration.save(file)
        }
        return ReadOnlyProperty { ref, prop -> getValue(ref, prop) }
    }
}

private infix fun <T : Any> String.toType(kClass: KClass<T>): T {
    return when (kClass) {
        Int::class -> this.toInt()
        Long::class -> this.toLong()
        Double::class -> this.toDouble()
        Float::class -> this.toFloat()
        Boolean::class -> this.toBoolean()
        else -> this
    } as T
}