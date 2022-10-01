package de.hglabor.hcfcore.manager

interface IManager<K, V> {
    val cache: MutableMap<K, V>

    fun enable()
    fun disable()
}