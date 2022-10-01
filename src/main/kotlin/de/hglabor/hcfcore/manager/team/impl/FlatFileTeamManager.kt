package de.hglabor.hcfcore.manager.team.impl

import de.hglabor.hcfcore.database.flatfile.FlatFileDatabase
import de.hglabor.hcfcore.team.ITeam
import de.hglabor.hcfcore.team.impl.KothTeam
import de.hglabor.hcfcore.team.impl.PlayerTeam
import de.hglabor.hcfcore.team.impl.SpawnTeam
import de.hglabor.hcfcore.manager.team.ITeamManager
import de.hglabor.hcfcore.team.impl.WarzoneTeam
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.StringFormat
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

open class FlatFileTeamManager : ITeamManager, FlatFileDatabase("teams") {
    override val cache: MutableMap<String, ITeam> = mutableMapOf()
    override val stringFormat: StringFormat = Json {
        serializersModule = SerializersModule {
            polymorphic(ITeam::class, PlayerTeam::class, PlayerTeam.serializer())
            polymorphic(ITeam::class, SpawnTeam::class, SpawnTeam.serializer())
            polymorphic(ITeam::class, KothTeam::class, KothTeam.serializer())
            polymorphic(ITeam::class, WarzoneTeam::class, WarzoneTeam.serializer())
        }
        encodeDefaults = true
    }

    override fun enable() {
        CoroutineScope(Dispatchers.IO).launch {
            fetchDatabase()
        }
        super.enable()
    }

    override fun disable() {
        CoroutineScope(Dispatchers.IO).launch {
            saveDatabase()
        }
    }

    override suspend fun fetchDatabase() {
        val jsonString = file.readText()
        if (jsonString.isBlank()) return

        cache.putAll(stringFormat.decodeFromString<List<ITeam>>(jsonString).associateBy { it.name }.toMutableMap())
    }


    override suspend fun saveDatabase() {
        file.writeText(stringFormat.encodeToString(cache.values))
    }
}