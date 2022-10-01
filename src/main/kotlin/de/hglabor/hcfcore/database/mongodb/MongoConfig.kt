package de.hglabor.hcfcore.database.mongodb

object MongoConfig {
    private val host by MongoFileDelegate()
    private val port by MongoFileDelegate()
    private val username by MongoFileDelegate()
    private val password by MongoFileDelegate()
    val database by MongoFileDelegate()
    private val encodedPassword = password.replace("%(?![0-9a-fA-F]{2})".toRegex(), "%25")

    fun uri(): String = "mongodb://$username:$encodedPassword@$host:${port.toInt()}/$database"
}