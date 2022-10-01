package de.hglabor.hcfcore.manager.player.impl

import com.mongodb.client.model.UpdateOptions
import de.hglabor.hcfcore.database.mongodb.MongoDatabase
import de.hglabor.hcfcore.database.mongodb.MongoManager
import de.hglabor.hcfcore.manager.team.impl.MongoTeamManager
import de.hglabor.hcfcore.manager.player.IPlayerManager
import de.hglabor.hcfcore.player.impl.TeamPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import java.util.*

open class MongoPlayerManager: IPlayerManager, MongoDatabase<TeamPlayer>() {
    override lateinit var collection: CoroutineCollection<TeamPlayer>
    override val cache: MutableMap<UUID, TeamPlayer> = mutableMapOf()

    override fun enable() {
        CoroutineScope(Dispatchers.IO).launch {
            collection = MongoManager.loadCollection("players")
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
        collection = MongoManager.loadCollection("claims")
        cache.putAll(collection.find().toList().associateBy { fp -> fp.uuid }.toMutableMap())
    }

    override suspend fun saveDatabase() {
        cache.values.forEach { fp ->
            collection.updateOne(TeamPlayer::uuid eq fp.uuid, fp, UpdateOptions().upsert(true))
        }
    }
}