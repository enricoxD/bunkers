package de.hglabor.hcfcore.team.impl

import de.hglabor.hcfcore.team.ITeam
import de.hglabor.hcfcore.team.claim.impl.KothClaim
import kotlinx.serialization.Serializable
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import org.bukkit.World
import org.bukkit.entity.Player

@Serializable
class KothTeam(
    override var name: String,
    override val claim: KothClaim
): ITeam {

    override fun sendInfo(player: Player) {
        val loc = claim.region.centerLocation
        val x = loc.blockX
        val z = loc.blockZ

        val strike = literalText { text("-------------------------") { color = KColors.DARKGRAY; strikethrough = true } }
        player.sendMessage(strike)
        player.sendMessage(literalText {
            text(name) { color = KColors.RED }
            text(" KOTH") { color = KColors.DARKRED }
        })
        player.sendMessage(literalText {
            text("Location: ") { color = KColors.GRAY }
            text("$x $z") { color = KColors.FLORALWHITE }
            if (claim.region.world.environment != World.Environment.NORMAL) {
                text(" (${claim.region.world.environment.name.lowercase().replaceFirstChar { it.uppercaseChar() }})") {
                    color = KColors.LIGHTGRAY
                }
            }
        })
        player.sendMessage(strike)
    }

    override fun disband() {
        // Unregister Koth
        super.disband()
    }
}