package de.hglabor.bunkers.mechanics

import de.hglabor.bunkers.teams.BunkersTeam
import de.hglabor.common.extension.broadcast
import de.hglabor.hcfcore.manager.player.teamPlayer
import de.hglabor.hcfcore.player.impl.TeamPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.extensions.bukkit.bukkitColor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.TextColor
import org.bukkit.event.entity.PlayerDeathEvent

object DeathMessages {
    fun announce(event: PlayerDeathEvent) {
        val player = event.player
        val teamPlayer = player.teamPlayer

        if (event.entity.killer != null) {
            announce(teamPlayer, player.killer?.teamPlayer!!)
        } else {
            if (event.deathMessage() == null) {
                announce(teamPlayer, event.deathMessage())
            } else {
                announce(teamPlayer)
            }
        }
        event.deathMessage(null)
    }

    private fun announce(killer: TeamPlayer, dead: TeamPlayer) {
        broadcast {
            component(formattedPlayer(dead, KColors.RED))
            text(" was slain by ") { color = KColors.GRAY }
            component(formattedPlayer(killer, KColors.GREEN))
        }
    }

    private fun announce(dead: TeamPlayer) {
        broadcast {
            component(formattedPlayer(dead, KColors.RED))
            text(" died") { color = KColors.GRAY }
        }
    }

    private fun announce(dead: TeamPlayer, deathMessage: Component?) {
        if (deathMessage == null) {
            announce(dead)
            return
        }

        deathMessage.color(KColors.GRAY)
        deathMessage.replaceText(
            TextReplacementConfig
                .builder()
                .matchLiteral(dead.name).replacement(formattedPlayer(dead, KColors.RED))
                .build()
        )

        broadcast {
            component(deathMessage)
        }
    }

    private fun formattedPlayer(teamPlayer: TeamPlayer, fallbackColor: TextColor): Component {
        return literalText {
            text(teamPlayer.name) { color = (teamPlayer.team as? BunkersTeam)?.teamColor ?: fallbackColor }
            text("[") { color = KColors.DARKGRAY }
            text("${teamPlayer.statistics.kills}") { color = KColors.WHITE }
            text("]") { color = KColors.DARKGRAY }
        }
    }
}