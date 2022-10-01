package de.hglabor.hcfcore.manager.claim.impl

import com.mongodb.client.model.UpdateOptions
import de.hglabor.hcfcore.database.mongodb.MongoDatabase
import de.hglabor.hcfcore.team.claim.IClaim
import de.hglabor.hcfcore.manager.claim.IClaimManager
import de.hglabor.hcfcore.team.claim.selection.ClaimSelectionManager
import de.hglabor.hcfcore.database.mongodb.MongoManager
import de.hglabor.hcfcore.manager.team.impl.MongoTeamManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.axay.kspigot.runnables.KSpigotRunnable
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq

open class MongoClaimManager: IClaimManager, MongoDatabase<IClaim>() {
    override lateinit var collection: CoroutineCollection<IClaim>

    override val cache: MutableMap<String, IClaim> = mutableMapOf()
    override val claimSelectionManager = ClaimSelectionManager
    override var playerChangeClaimTask: KSpigotRunnable? = null

    override fun enable() {
        CoroutineScope(Dispatchers.IO).launch {
            collection = MongoManager.loadCollection("claims")
            fetchDatabase()
        }
        super.enable()
    }

    override fun disable() {
        CoroutineScope(Dispatchers.IO).launch {
            saveDatabase()
        }
        super.disable()
    }

    override suspend fun fetchDatabase() {
        collection = MongoManager.loadCollection("claims")
        cache.putAll(collection.find().toList().associateBy { claim -> claim.name }.toMutableMap())
    }

    override suspend fun saveDatabase() {
        cache.values.forEach { claim ->
            collection.updateOne(IClaim::name eq claim.name, claim, UpdateOptions().upsert(true))
        }
    }
}