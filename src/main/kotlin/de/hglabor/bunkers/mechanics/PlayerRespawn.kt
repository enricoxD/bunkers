package de.hglabor.bunkers.mechanics

import de.hglabor.bunkers.game.GameManager
import de.hglabor.bunkers.game.phase.phases.EndPhase
import de.hglabor.bunkers.game.phase.phases.IngamePhase
import de.hglabor.bunkers.teams.BunkersTeam
import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.event.koth.KothManager
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
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

object PlayerRespawn {
    val eliminatedPlayers = mutableSetOf<UUID>()
    val respawningPlayers = mutableSetOf<UUID>()

    fun enable() {
        listen<PlayerDeathEvent>(priority = EventPriority.LOW) {
            if (GameManager.currentPhase != IngamePhase) return@listen
            val player = it.player
            val team = player.teamPlayer.team ?: return@listen
            DeathMessages.announce(it)

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

        // TODO test and refactor this is SHIT

        listen<PlayerQuitEvent> {
            if (GameManager.currentPhase != IngamePhase) return@listen
            val player = it.player
            val team = player.teamPlayer.team ?: return@listen

            if (team.isRaidable && player.uniqueId in eliminatedPlayers) {
                return@listen
            } else if (player.uniqueId in respawningPlayers) {
                return@listen
            }

            respawningPlayers.add(player.uniqueId)
            if (team.dtr <= 0.0f) {
                eliminatedPlayers.add(player.uniqueId)
                checkForWinner()
                return@listen
            }

            team.dtr -= 1
            team.notify(false) {
                text("Member Quit: ") { color = KColors.RED }
                text(it.player.name)
                newLine()
                text("DTR: ") { color = KColors.RED }
                text(team.dtr.toString())
            }

            if (team.dtr <= 0.0 && !team.isRaidable) {
                team.isRaidable = true
                team.notify {
                    text("You are now ") { color = KColors.GRAY }
                    text("raidable") { color = KColors.RED; bold = true }
                    text("!") { color = KColors.GRAY }
                    newLine()
                    text("From now on, your death will be permanent.") { color = KColors.RED }
                }
            }
        }

        listen<PlayerJoinEvent> {
            if (GameManager.currentPhase != IngamePhase) return@listen
            val player = it.player
            val team = player.teamPlayer.team

            player.gameMode = GameMode.SPECTATOR
            if (team == null) {
                val koth = KothManager.currentKoth ?: return@listen
                player.teleport(koth.team.claim.captureZone.centerLocation)
            } else {
                player.teamPlayer.team?.homeLocation?.let { location ->
                    player.teleport(location)
                }
            }

            if (player.uniqueId in eliminatedPlayers) {
                player.title(literalText("You have been eliminated!") { color = KColors.RED })
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

    private fun checkForWinner() {
        val winner = Core.teamManager.teams.filterIsInstance<BunkersTeam>().singleOrNull { team ->
            team.members.any { uuid -> uuid !in eliminatedPlayers }
        } ?: return

        EndPhase.winner = winner
        GameManager.startNextPhase()
    }
}