package de.hglabor.hcfcore.database.flatfile

import de.hglabor.hcfcore.Manager
import de.hglabor.hcfcore.database.IDatabase
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import net.axay.kspigot.languageextensions.kotlinextensions.createIfNotExists
import java.io.File
import java.nio.file.Paths

abstract class FlatFileDatabase(fileName: String) : IDatabase {
    val file: File = Paths.get(Manager.dataFolder.toString(), "cache", "$fileName.json").toFile()
        .also { it.createIfNotExists() }

    open val stringFormat: StringFormat = Json
}