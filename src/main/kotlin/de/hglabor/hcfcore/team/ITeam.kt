package de.hglabor.hcfcore.team

import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.team.claim.IClaim
import org.bukkit.entity.Player

interface ITeam {
    val name: String
    val claim: IClaim?

    fun sendInfo(player: Player)

    fun disband() {
        Core.teamManager.cache.remove(name)
        claim?.let { c ->
            Core.claimManager.unclaim(c)
        }
    }
}
