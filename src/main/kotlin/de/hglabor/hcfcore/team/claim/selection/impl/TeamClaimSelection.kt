package de.hglabor.hcfcore.team.claim.selection.impl

import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.team.claim.impl.TeamClaim
import de.hglabor.hcfcore.team.claim.selection.AbstractClaimSelection
import de.hglabor.hcfcore.team.impl.PlayerTeam
import net.axay.kspigot.chat.KColors
import org.bukkit.Bukkit
import java.util.*

class TeamClaimSelection(creator: UUID, name: String): AbstractClaimSelection(creator, name) {

    override fun claim(): TeamClaim? {
        val player = Bukkit.getPlayer(creator) ?: return null
        val region = asRegion() ?: return null
        val team = Core.teamManager.teamByName(name) as? PlayerTeam ?: return null
        val price = Core.claimManager.calculatePrice(this, false)
        val members = team.members.toMutableSet()

        // Team doesn't have enough money
        if (team.balance < price) {
            player.sendMsg {
                text("Your team doesn't have enough money to do this!") { color = KColors.RED }
            }
            return null
        }

        return TeamClaim(members, region, name)
    }

    override fun confirm() {
        val player = Bukkit.getPlayer(creator) ?: return
        val claim = claim() ?: return
        val price = Core.claimManager.calculatePrice(this, false)
        val team = Core.teamManager.teamByName(name) as? PlayerTeam ?: return

        team.balance -= price
        player.sendMsg {
            text("You have claimed this land for your team!") { color = KColors.GRAY }
        }
        player.sendMsg {
            text("Your team's new balance is ") { color = KColors.GRAY }
            text("$${team.balance} ") { color = KColors.DARKAQUA }
            text("(Price: $$price)") { color = KColors.FLORALWHITE }
        }
        Core.claimManager.claimSelectionManager.clearSelection(creator, false)
        Core.claimManager.claim(claim)
    }
}