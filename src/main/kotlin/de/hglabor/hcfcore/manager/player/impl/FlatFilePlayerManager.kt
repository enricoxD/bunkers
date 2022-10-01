package de.hglabor.hcfcore.manager.player.impl

import de.hglabor.hcfcore.database.flatfile.FlatFileDatabase
import de.hglabor.hcfcore.manager.player.IPlayerManager
import de.hglabor.hcfcore.player.ITeamPlayer
import de.hglabor.hcfcore.player.impl.TeamPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.StringFormat
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import java.util.*

open class FlatFilePlayerManager: IPlayerManager, FlatFileDatabase("players") {
    override val cache: MutableMap<UUID, TeamPlayer> = mutableMapOf()
    override val stringFormat: StringFormat = Json {
        serializersModule = SerializersModule {
            polymorphic(ITeamPlayer::class, TeamPlayer::class, TeamPlayer.serializer())
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

        cache.putAll(stringFormat.decodeFromString<List<TeamPlayer>>(jsonString).associateBy { it.uuid }.toMutableMap())
    }

    override suspend fun saveDatabase() {
        file.writeText(stringFormat.encodeToString(cache.values))
    }
}