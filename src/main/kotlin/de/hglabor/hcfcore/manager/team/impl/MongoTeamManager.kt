package de.hglabor.hcfcore.manager.team.impl

import com.mongodb.client.model.UpdateOptions
import de.hglabor.hcfcore.database.mongodb.MongoDatabase
import de.hglabor.hcfcore.database.mongodb.MongoManager
import de.hglabor.hcfcore.team.ITeam
import de.hglabor.hcfcore.manager.team.ITeamManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq

open class MongoTeamManager : ITeamManager, MongoDatabase<ITeam>() {
    override lateinit var collection: CoroutineCollection<ITeam>
    override val cache: MutableMap<String, ITeam> = mutableMapOf()

    override fun enable() {
        CoroutineScope(Dispatchers.IO).launch {
            collection = MongoManager.loadCollection("teams")
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
        cache.putAll(collection.find().toList().associateBy { team -> team.name }.toMutableMap())
    }

    override suspend fun saveDatabase() {
        cache.values.forEach { team ->
            collection.updateOne(ITeam::name eq team.name, team, UpdateOptions().upsert(true))
        }
    }
}