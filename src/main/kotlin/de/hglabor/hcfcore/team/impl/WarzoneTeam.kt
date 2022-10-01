package de.hglabor.hcfcore.team.impl

import de.hglabor.hcfcore.team.ITeam
import de.hglabor.hcfcore.team.claim.impl.WarzoneClaim
import kotlinx.serialization.Serializable
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import org.bukkit.entity.Player

@Serializable
class WarzoneTeam(
    override val name: String,
    override val claim: WarzoneClaim
) : ITeam {

    override fun sendInfo(player: Player) {
        val strike =
            literalText { text("-------------------------") { color = KColors.DARKGRAY; strikethrough = true } }
        player.sendMessage(strike)
        player.sendMessage(literalText("Warzone") { color = KColors.RED })
        player.sendMessage(literalText {
            text("Size: ") { color = KColors.GRAY }
            text("${claim.radius}, ${claim.radius}") { color = KColors.FLORALWHITE }
        })
        player.sendMessage(strike)
    }
}