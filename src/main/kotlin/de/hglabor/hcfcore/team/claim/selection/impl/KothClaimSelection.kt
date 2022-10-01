package de.hglabor.hcfcore.team.claim.selection.impl

import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.team.claim.Region
import de.hglabor.hcfcore.team.claim.impl.KothClaim
import de.hglabor.hcfcore.team.claim.selection.AbstractClaimSelection
import de.hglabor.hcfcore.team.claim.selection.OverlappingClaimSelection
import de.hglabor.hcfcore.team.impl.KothTeam
import de.hglabor.hcfcore.visualization.VisualizationHandler
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.extensions.geometry.add
import org.bukkit.Bukkit
import java.util.*

class KothClaimSelection(creator: UUID, name: String): AbstractClaimSelection(creator, name), OverlappingClaimSelection {
    var teamRegion: Region? = null
    var captureZone: Region? = null

    override fun claim(): KothClaim? {
        var region = asRegion() ?: return null
        val player = Bukkit.getPlayer(creator) ?: return null
        if (teamRegion == null) {
            teamRegion = region
            player.sendMsg {
                text("Successfully ") { color = KColors.GREEN }
                text("set the claim of the ") { color = KColors.GRAY }
                text("KOTH") { color = KColors.RED }
                text(".") { color = KColors.GRAY }
            }
            player.sendMessage(literalText {
                text("! Note") { color = KColors.RED; bold = true }
                text(": You need to set another region which will represent the capture zone!") { color = KColors.WHITE; bold = true }
            })
            VisualizationHandler.hideVisuals(player.uniqueId)
            loc1 = null
            loc2 = null
            return null
        }

        if (captureZone == null) {
            if (region.loc1.blockY != region.loc2.blockY) {
                player.sendMsg {
                    text("The corners of the capture zone should be on the same height!") { color = KColors.RED }
                    newLine()
                    text("The KOTH may be captured within a height distance of up to 5 blocks (upwards).") { color = KColors.WHITE }
                }
                return null
            }
            loc2 = loc2!!.add(0, 5, 0)
            captureZone = asRegion()!!

            player.sendMsg {
                text("Successfully ") { color = KColors.GREEN }
                text("set the ") { color = KColors.GRAY }
                text("capture zone") { color = KColors.RED }
                text(".") { color = KColors.GRAY }
            }
            loc1 = null
            loc2 = null
        }

        val facRegion = teamRegion ?: return null
        val capZone = captureZone ?: return null
        return KothClaim(name, facRegion, capZone)
    }

    override fun confirm() {
        val player = Bukkit.getPlayer(creator) ?: return
        val claim = claim() ?: return

        player.sendMsg {
            text("You have created the ") { color = KColors.GRAY }
            text("KOTH ") { color = KColors.RED }
            text(name) { color = KColors.DARKRED }
            text("!") { color = KColors.GRAY }
        }

        Core.claimManager.claimSelectionManager.clearSelection(creator, false)
        Core.claimManager.claim(claim)
        Core.teamManager.cache[name] = KothTeam(name, claim)
    }
}