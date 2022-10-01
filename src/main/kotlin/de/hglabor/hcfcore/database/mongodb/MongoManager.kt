package de.hglabor.hcfcore.database.mongodb

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.reactivestreams.client.MongoClients
import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.Manager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.bson.UuidRepresentation
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.service.ClassMappingType

object MongoManager {
    lateinit var client: CoroutineClient
    lateinit var database: CoroutineDatabase
    lateinit var connectJob: Job

    fun connect(callBack: (suspend () -> Unit)? = null) {
        connectJob = CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                initialize(callBack)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    private suspend fun initialize(callBack: (suspend () -> Unit)? = null) {
        runCatching {
            System.setProperty(
                "org.litote.mongo.test.mapping.service",
                "org.litote.kmongo.serialization.SerializationClassMappingTypeService"
            )
            val connectionString = ConnectionString(MongoConfig.uri())
            val clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .codecRegistry(ClassMappingType.codecRegistry(MongoClientSettings.getDefaultCodecRegistry()))
                .build()

            client = MongoClients.create(clientSettings).coroutine
            database = client.getDatabase(MongoConfig.database)
        }.onSuccess {
            callBack?.invoke()
            Manager.logger.info("Successfully connected to Mongodatabase ${database.name}")
        }.onFailure {
            it.printStackTrace()
        }
    }

    fun disconnect() = runCatching { client.close() }

    suspend inline fun <reified T : Any> loadCollection(name: String): CoroutineCollection<T> {
        if (!database.listCollectionNames().contains(name)) database.createCollection(name)
        return database.getCollection(name)
    }
}