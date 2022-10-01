package de.hglabor.hcfcore.team.claim

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.axay.kspigot.extensions.geometry.*
import net.axay.kspigot.serialization.LocationSerializer
import org.bukkit.Location
import org.bukkit.World

@Serializable
class Region(
    @Serializable(with = LocationSerializer::class)
    val loc1: Location,
    @Serializable(with = LocationSerializer::class)
    val loc2: Location,
) {
    @Transient
    val simpleLocationPair = SimpleLocationPair(loc1, loc2)

    val world: World get() = simpleLocationPair.world
    val centerLocation: Location get() {
        val x = (loc1.x + loc2.x) / 2
        val y = (loc1.y + loc2.y) / 2
        val z = (loc1.z + loc2.z) / 2

        return Location(world, x, y, z)
    }

    private fun xDistance(): Int {
        val loc1 = loc1.blockX
        val loc2 = loc2.blockX
        return kotlin.math.abs(loc1 - loc2)
    }

    private fun zDistance(): Int {
        val loc1 = loc1.blockZ
        val loc2 = loc2.blockZ
        return kotlin.math.abs(loc1 - loc2)
    }

    fun totalBlocks(): Int {
        return xDistance() * zDistance()
    }

    fun isInArea(
        loc: Location,
        check3d: Boolean = false,
        tolerance: Int = 0,
    ) = simpleLocationPair.isInArea(loc, check3d, tolerance)
}