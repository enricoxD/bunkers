package de.hglabor.hcfcore.commands

import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.manager.player.teamPlayer
import de.hglabor.hcfcore.manager.player.teamPlayerByName
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.commands.argument
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.runs
import net.axay.kspigot.commands.suggestListSuspending
import net.axay.kspigot.extensions.onlinePlayers

object PayCommand {
    fun register() {
        command("pay") {
            argument<String>("player") {
                suggestListSuspending { onlinePlayers.map { it.name } }
                argument<Int>("amount") {
                    suggestListSuspending { listOf(100, 500, 1000) }
                    runs {
                        val targetName = getArgument<String>("player")
                        val amount = getArgument<Int>("amount")

                        if (player.teamPlayer.balance < amount) {
                            player.sendMsg {
                                text("You don't have enough money to do this!") { color = KColors.RED }
                            }
                            return@runs
                        }

                        val targetTeamPlayer = teamPlayerByName(targetName)
                        // Target never joined the server
                        if (targetTeamPlayer == null) {
                            player.sendMsg {
                                text("$targetName has never joined the server.") { color = KColors.RED }
                            }
                            return@runs
                        }

                        player.teamPlayer.balance -= amount
                        player.sendMsg {
                            text("You sent ") { color = KColors.GRAY }
                            text("$$amount ") { color = KColors.DARKAQUA }
                            text("to ") { color = KColors.GRAY }
                            text("${targetTeamPlayer.name} ") { color = KColors.AQUAMARINE }
                            text(".") { color = KColors.GRAY }
                        }
                        targetTeamPlayer.balance += amount
                        targetTeamPlayer.sendMsg {
                            text("${player.name} ") { color = KColors.AQUAMARINE }
                            text("send you ") { color = KColors.GRAY }
                            text("$$amount") { color = KColors.AQUAMARINE }
                            text(".") { color = KColors.GRAY }
                        }
                    }
                }
            }
        }
    }
}