package de.hglabor.bunkers.creation

import de.hglabor.bunkers.teams.BunkersTeam
import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.team.claim.impl.TeamClaim
import de.hglabor.hcfcore.team.claim.selection.AbstractClaimSelection
import de.hglabor.hcfcore.team.claim.selection.OverlappingClaimSelection
import net.axay.kspigot.chat.KColors
import org.bukkit.Bukkit
import java.util.*

class BunkersClaimSelection(creator: UUID, val team: BunkersTeam): AbstractClaimSelection(creator, team.name), OverlappingClaimSelection {

    override fun claim(): TeamClaim? {
        var region = asRegion() ?: return null
        return TeamClaim(mutableSetOf(), region, name)
    }

    override fun confirm() {
        val player = Bukkit.getPlayer(creator) ?: return
        val claim = claim() ?: return

        player.sendMsg {
            text("You have claimed land for team ") { color = KColors.GRAY }
            text(name) { color = team.teamColor }
            text("!") { color = KColors.GRAY }
        }

        Core.claimManager.claimSelectionManager.clearSelection(creator, false)
        Core.claimManager.getClaimOf(name)?.unclaim()
        Core.claimManager.claim(claim)
    }
}