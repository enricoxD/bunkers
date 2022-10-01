package de.hglabor.hcfcore.team.claim.selection.impl

import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.team.claim.impl.SpawnClaim
import de.hglabor.hcfcore.team.claim.selection.AbstractClaimSelection
import de.hglabor.hcfcore.team.claim.selection.OverlappingClaimSelection
import de.hglabor.hcfcore.team.impl.SpawnTeam
import net.axay.kspigot.chat.KColors
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.*

class SpawnClaimSelection(creator: UUID, name: String): AbstractClaimSelection(creator, name), OverlappingClaimSelection {

    override fun claim(): SpawnClaim? {
        val region = asRegion() ?: return null
        val player = Bukkit.getPlayer(creator) ?: return null
        if (!region.isInArea(Location(region.world, 0.0, 100.0, 0.0))) {
            player.sendMsg {
                text("The spawn needs to be located around 0 0") { color = KColors.RED }
            }
            return null
        }
        return SpawnClaim(name, region)
    }

    override fun confirm() {
        val player = Bukkit.getPlayer(creator) ?: return
        val claim = claim() ?: return

        player.sendMsg {
            text("You have claimed this land for ") { color = KColors.GRAY }
            text(name) { color = KColors.AQUAMARINE }
            text("!") { color = KColors.GRAY }
        }

        Core.claimManager.claimSelectionManager.clearSelection(creator, false)
        Core.claimManager.claim(claim)
        Core.teamManager.cache[name] = SpawnTeam(name, claim)
    }
}