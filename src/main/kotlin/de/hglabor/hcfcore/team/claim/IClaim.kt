package de.hglabor.hcfcore.team.claim

import de.hglabor.hcfcore.Core
import org.bukkit.Location

interface IClaim {
    var name: String
    val region: Region

    fun isWithin(
        loc: Location,
        tolerance: Int
    ) = region.isInArea(loc, false, tolerance)

    fun unclaim() {
        Core.claimManager.unclaim(this)
    }
}