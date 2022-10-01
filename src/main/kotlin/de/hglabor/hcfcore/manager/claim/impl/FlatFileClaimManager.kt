package de.hglabor.hcfcore.manager.claim.impl

import de.hglabor.hcfcore.database.flatfile.FlatFileDatabase
import de.hglabor.hcfcore.team.claim.IClaim
import de.hglabor.hcfcore.team.claim.impl.TeamClaim
import de.hglabor.hcfcore.team.claim.impl.KothClaim
import de.hglabor.hcfcore.team.claim.impl.SpawnClaim
import de.hglabor.hcfcore.team.claim.selection.ClaimSelectionManager
import de.hglabor.hcfcore.manager.claim.IClaimManager
import de.hglabor.hcfcore.team.claim.impl.WarzoneClaim
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.runnables.KSpigotRunnable

open class FlatFileClaimManager: IClaimManager, FlatFileDatabase("claims") {
    override val stringFormat = Json {
        serializersModule = SerializersModule {
            polymorphic(IClaim::class, TeamClaim::class, TeamClaim.serializer())
            polymorphic(IClaim::class, KothClaim::class, KothClaim.serializer())
            polymorphic(IClaim::class, SpawnClaim::class, SpawnClaim.serializer())
            polymorphic(IClaim::class, WarzoneClaim::class, WarzoneClaim.serializer())
        }
        encodeDefaults = true
    }

    override val cache: MutableMap<String, IClaim> = mutableMapOf()
    override val claimSelectionManager = ClaimSelectionManager
    override var playerChangeClaimTask: KSpigotRunnable? = null


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

        cache.putAll(stringFormat.decodeFromString<List<IClaim>>(jsonString).associateBy { it.name }.toMutableMap())
    }

    override suspend fun saveDatabase() {
        broadcast("Claims: ${cache.values.size}")
        file.writeText(stringFormat.encodeToString(cache.values))
    }
}
