package de.hglabor.hcfcore.team.claim.impl

import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.team.claim.IOverlappingClaim
import de.hglabor.hcfcore.team.claim.Region
import kotlinx.serialization.Serializable
import org.bukkit.entity.Player

@Serializable
class KothClaim(
    override var name: String,
    override val region: Region,
    val captureZone: Region
) : IOverlappingClaim {

    fun isInCaptureZone(player: Player): Boolean {
        return captureZone.isInArea(player.location, true)
    }

    override fun unclaim() {
        Core.teamManager.teamByName(name)?.disband()
    }
}