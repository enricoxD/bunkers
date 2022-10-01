package de.hglabor.hcfcore.database

interface IDatabase {
    suspend fun fetchDatabase()
    suspend fun saveDatabase()
}