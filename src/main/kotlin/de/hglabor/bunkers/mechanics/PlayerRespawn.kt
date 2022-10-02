package de.hglabor.bunkers.mechanics

import de.hglabor.bunkers.game.GameManager
import de.hglabor.bunkers.game.phase.phases.EndPhase
import de.hglabor.bunkers.teams.BunkersTeam
import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.manager.player.teamPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.bukkit.feedSaturate
import net.axay.kspigot.extensions.bukkit.heal
import net.axay.kspigot.extensions.bukkit.title
import net.axay.kspigot.runnables.task
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.GameMode
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.PlayerDeathEvent
import java.util.*

object PlayerRespawn {
    val eliminatedPlayers = mutableSetOf<UUID>()
    val respawningPlayers = mutableSetOf<UUID>()

    fun enable() {
        listen<PlayerDeathEvent>(priority = EventPriority.LOW) {
            val player = it.player
            val team = player.teamPlayer.team ?: return@listen
            respawningPlayers.add(player.uniqueId)
            taskRunLater(1) {
                player.gameMode = GameMode.SPECTATOR
                player.spigot().respawn()
                player.teamPlayer.team?.homeLocation?.let { location ->
                    player.teleport(location)
                }
            }

            if (team.dtr <= 0.0f) {
                player.title(literalText("You have been eliminated!") { color = KColors.RED })
                eliminatedPlayers.add(player.uniqueId)
                checkForWinner()
                return@listen
            }

            task(true, 20, 20, 5, endCallback = {
                taskRunLater(20) {
                    respawningPlayers.remove(player.uniqueId)
                    player.clearTitle()
                    player.teamPlayer.team?.homeLocation?.let { location ->
                        player.teleport(location)
                    }
                    player.gameMode = GameMode.SURVIVAL
                    player.inventory.clear()
                    player.fireTicks = 0
                    player.heal()
                    player.feedSaturate()
                }
            }) {
                player.title(
                    literalText("${it.counterDownToOne}"),
                    literalText("Respawning in") { color = KColors.GRAY }
                )
            }
        }
    }

    fun checkForWinner() {
        val winner = Core.teamManager.teams.filterIsInstance<BunkersTeam>().singleOrNull { team ->
            team.members.any { uuid -> uuid !in eliminatedPlayers }
        } ?: return

        EndPhase.winner = winner
        GameManager.startNextPhase()
    }
}