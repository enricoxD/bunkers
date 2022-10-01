package de.hglabor.hcfcore.chat

import de.hglabor.bunkers.teams.BunkersTeam
import de.hglabor.hcfcore.manager.player.teamPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.runnables.async
import org.bukkit.entity.Player

enum class ChatChannel {
    PUBLIC, TEAM, CAPTAIN;

    fun sendMessage(player: Player, message: String) {
        val teamPlayer = player.teamPlayer
        val team = teamPlayer.team

        if (this == PUBLIC || team == null) {
            async {
                onlinePlayers.forEach { player ->
                    player.sendMessage(literalText {
                        if (teamPlayer.teamName != null) {
                            // TODO remove when seperating hcfcore from bunkers
                            val teamColor = (teamPlayer.team as? BunkersTeam)?.teamColor ?: KColors.DARKAQUA

                            text("[") { color = KColors.DARKGRAY }
                            text("${teamPlayer.teamName}") { color = teamColor }
                            text("] ") { color = KColors.DARKGRAY }
                        }
                        text("${teamPlayer.name} ") { color = KColors.WHITE } // TODO use color of rank
                        text("» ") { color = KColors.DARKGRAY }
                        text(message) { color = KColors.WHITE }
                    })
                }
            }
            return
        }

        when (this) {
            TEAM -> {
                async {
                    team.players.forEach { member ->
                        member.sendMessage(literalText {
                            text("[Team] ${player.name} ") { color = KColors.HOTPINK }
                            text("» ") { color = KColors.DARKGRAY }
                            text(message) { color = KColors.DARKMAGENTA }
                        })
                    }
                }
            }

            CAPTAIN -> {
                async {
                    team.teamPlayers.filter { it.teamRole?.hasBasicPermission == true }
                        .mapNotNull { it.player }
                        .forEach { member ->
                            member.sendMessage(literalText {
                                text("[Captain] ${player.name} ") { color = KColors.ORANGE }
                                text("» ") { color = KColors.DARKGRAY }
                                text(message) { color = KColors.YELLOW }
                            })
                        }
                }
            }
            else -> return
        }
    }
}