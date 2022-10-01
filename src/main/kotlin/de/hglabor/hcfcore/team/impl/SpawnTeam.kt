package de.hglabor.hcfcore.team.impl

import de.hglabor.hcfcore.team.ITeam
import de.hglabor.hcfcore.team.claim.impl.SpawnClaim
import kotlinx.serialization.Serializable
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import org.bukkit.World
import org.bukkit.entity.Player

@Serializable
class SpawnTeam(
    override val name: String,
    override val claim: SpawnClaim
) : ITeam {

    override fun sendInfo(player: Player) {
        val strike =
            literalText { text("-------------------------") { color = KColors.DARKGRAY; strikethrough = true } }
        player.sendMessage(strike)
        player.sendMessage(literalText("Spawn") { color = KColors.LIME })
        player.sendMessage(literalText {
            text("Location: ") { color = KColors.GRAY }
            text("0, 0") { color = KColors.FLORALWHITE }
            if (claim.region.world.environment != World.Environment.NORMAL) {
                text(" (${claim.region.world.environment.name.lowercase().replaceFirstChar { it.uppercaseChar() }})") {
                    color = KColors.LIGHTGRAY
                }
            }
        })
        player.sendMessage(strike)
    }
}