package de.hglabor.hcfcore.database.mongodb

import de.hglabor.hcfcore.database.IDatabase
import org.litote.kmongo.coroutine.CoroutineCollection

abstract class MongoDatabase<T : Any> : IDatabase {
    abstract val collection: CoroutineCollection<T>
}