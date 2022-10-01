package de.hglabor.hcfcore.team.claim.selection

import de.hglabor.hcfcore.team.claim.IClaim
import de.hglabor.hcfcore.team.claim.Region
import org.bukkit.Location
import java.util.UUID
import kotlin.math.max
import kotlin.math.min

abstract class AbstractClaimSelection(val creator: UUID, val name: String) {
    var loc1: Location? = null
        set(value) {
            field = value
            lastUpdate = System.currentTimeMillis()
        }
    var loc2: Location? = null
        set(value) {
            field = value
            lastUpdate = System.currentTimeMillis()
        }

    var lastUpdate = System.currentTimeMillis() - 5

    fun asRegion(): Region? {
        val loc1 = loc1?.toCenterLocation() ?: return null
        val loc2 = loc2?.toCenterLocation() ?: return null
        val world = loc1.world
        
        val minLoc = Location(world, min(loc1.x, loc2.x), min(loc1.y, loc2.y), min(loc1.z, loc2.z)).add(-0.5, 0.0, -0.5)
        val maxLoc = Location(world, max(loc1.x, loc2.x), max(loc1.y, loc2.y), max(loc1.z, loc2.z)).add(0.5, 0.0, 0.5)
        
        return Region(minLoc, maxLoc)
    }

    abstract fun claim(): IClaim?
    abstract fun confirm()

    fun xDistance(): Int {
        val loc1 = loc1?.blockX ?: loc2?.blockX ?: 0
        val loc2 = loc2?.blockX ?: loc1
        return kotlin.math.abs(loc1 - loc2)
    }

    fun zDistance(): Int {
        val loc1 = loc1?.blockZ ?: loc2?.blockZ ?: 0
        val loc2 = loc2?.blockZ ?: loc1
        return kotlin.math.abs(loc1 - loc2)
    }

    fun totalBlocks(): Int {
        return xDistance() * zDistance()
    }
}