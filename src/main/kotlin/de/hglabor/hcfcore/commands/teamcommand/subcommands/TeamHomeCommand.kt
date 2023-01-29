package de.hglabor.hcfcore.commands.teamcommand.subcommands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import de.hglabor.common.extension.sendMsg
import de.hglabor.common.scoreboard.Board
import de.hglabor.common.scoreboard.ScoreboardManager
import de.hglabor.hcfcore.commands.teamcommand.ITeamCommand
import de.hglabor.hcfcore.commands.teamcommand.TeamCommandCategory
import de.hglabor.hcfcore.listener.event.team.PlayerLeaveTeamEvent
import de.hglabor.hcfcore.manager.player.teamPlayer
import kotlinx.coroutines.*
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.commands.runs
import net.axay.kspigot.event.listen
import net.axay.kspigot.runnables.sync
import net.minecraft.commands.CommandSourceStack
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import java.util.*

object TeamHomeCommand: ITeamCommand(TeamCommandCategory.INFORMATION, "home", "h") {
    private const val TELEPORTATION_TIME = 15000L
    override val usage: String = "/t home"
    override val description: String = "Teleport to your team's home"

    private val teleportationTimestamps = mutableMapOf<UUID, Long>()
    private val countdownBoardLine = mutableMapOf<UUID, Board.BoardLine>()
    private val coroutineJobs = mutableMapOf<UUID, Job>()

    override val commandCallback: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit = {
        runs {
            val teamPlayer = player.teamPlayer
            val team = teamPlayer.team

            // Sender is not on a team
            if (team == null) {
                player.sendMsg {
                    text("You are not on a team!") { color = KColors.RED }
                }
                return@runs
            }

            val homeLocation = team.homeLocation

            // Team doesn't have a home
            if (homeLocation == null) {
                player.sendMsg {
                    text("Your team doesn't have a home!") { color = KColors.RED }
                }
                return@runs
            }

            // Sender already teleporting
            if (player.uniqueId in teleportationTimestamps) {
                player.sendMsg {
                    text("You are already teleporting!") { color = KColors.RED }
                }
                return@runs
            }

            startTeleportation(player, homeLocation)
        }
    }

    private fun startTeleportation(player: Player, location: Location) {
        ScoreboardManager.boards[player.uniqueId]?.let { board ->
            countdownBoardLine[player.uniqueId] = board.addLineBelow {
                literalText {
                    text("Teleporting: ") { color = KColors.YELLOW; bold = true }
                    val remainingTime = (teleportationTimestamps[player.uniqueId] ?: 0) - System.currentTimeMillis()
                    if (remainingTime > 0)
                        text("${remainingTime / 1000.0}s") { color = KColors.RED; bold = true }
                    else
                        text("0.0s") { color = KColors.RED; bold = true }
                }
            }
        }

        teleportationTimestamps[player.uniqueId] = System.currentTimeMillis() + TELEPORTATION_TIME
        player.sendMsg {
            text("You will be teleported to your ") { color = KColors.GRAY }
            text("team's home ") { color = KColors.AQUAMARINE }
            text("in ") { color = KColors.GRAY }
            text("${TELEPORTATION_TIME / 1000} seconds") { color = KColors.DARKAQUA }
            text(".") { color = KColors.GRAY }
        }

        CoroutineScope(Dispatchers.IO).launch {
            delay(TELEPORTATION_TIME)
            val teleportationTime = teleportationTimestamps[player.uniqueId] ?: return@launch
            if (teleportationTime <= System.currentTimeMillis()) {
                sync {
                    player.teleport(location)
                }
                teleportationTimestamps.remove(player.uniqueId)
                coroutineJobs.remove(player.uniqueId)
                countdownBoardLine.remove(player.uniqueId)?.unregister(true)
                player.sendMsg {
                    text("You have been teleported to your ") { color = KColors.GRAY }
                    text("team's home") { color = KColors.AQUAMARINE }
                    text(".") { color = KColors.GRAY }
                }
            }
        }
    }

    private fun cancelTeleportation(player: Player) {
        teleportationTimestamps.remove(player.uniqueId)
        countdownBoardLine.remove(player.uniqueId)?.unregister(true)
        player.sendMsg {
            text("Your teleport has been ") { color = KColors.GRAY }
            text("cancelled") { color = KColors.RED }
            text(".") { color = KColors.GRAY }
        }
    }

    init {
        listen<PlayerMoveEvent> {
            val player = it.player
            if (player.uniqueId !in teleportationTimestamps) return@listen
            val from = it.from
            val to = it.to
            if (from.x != to.x || from.z != to.z) {
                cancelTeleportation(player)
            }
        }

        listen<PlayerQuitEvent> {
            cancelTeleportation(it.player)
        }

        listen<PlayerLeaveTeamEvent> {
            cancelTeleportation(it.player)
        }

        listen<PlayerTeleportEvent> {
            cancelTeleportation(it.player)
        }

        listen<ProjectileLaunchEvent> {
            if (it.entityType != EntityType.ENDER_PEARL) return@listen
            val player = it.entity.shooter as? Player ?: return@listen
            cancelTeleportation(player)
        }
    }
}