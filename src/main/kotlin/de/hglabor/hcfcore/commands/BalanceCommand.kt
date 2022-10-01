package de.hglabor.hcfcore.commands

import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.manager.player.teamPlayer
import de.hglabor.hcfcore.manager.player.teamPlayerByName
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.commands.*
import net.axay.kspigot.extensions.onlinePlayers

object BalanceCommand {
    fun register() {
        command("balance") {
            argument<String>("player") {
                suggestListSuspending { onlinePlayers.map { it.name } }
                runs {
                    val targetName = getArgument<String>("player")
                    val teamTarget = teamPlayerByName(targetName)

                    if (teamTarget == null) {
                        player.sendMsg {
                            text("$targetName has never joined the server.") { color = KColors.RED }
                        }
                        return@runs
                    }

                    player.sendMsg {
                        text("Balance of ${teamTarget.name}: ") { color = KColors.GRAY }
                        text("$${player.teamPlayer.balance}") { color = KColors.DARKAQUA }
                    }
                }
            }

            literal("set") {
                requiresPermission("hglabor.staff")
                argument<String>("player") {
                    argument<Int>("amount") {
                        runs {
                            val targetName = getArgument<String>("player")
                            val teamTarget = teamPlayerByName(targetName)

                            if (teamTarget == null) {
                                player.sendMsg {
                                    text("$targetName has never joined the server.") { color = KColors.RED }
                                }
                                return@runs
                            }

                            val amount = getArgument<Int>("amount")
                            teamTarget.balance = amount
                            player.sendMsg {
                                text("Successfully set ") { color = KColors.GRAY }
                                text("${teamTarget.name}'s ") { color = KColors.AQUAMARINE }
                                text("balance to ") { color = KColors.GRAY }
                                text("$$amount") { color = KColors.DARKAQUA }
                                text(".") { color = KColors.GRAY }
                            }
                        }
                    }
                }
            }

            runs {
                player.sendMsg {
                    text("Balance: ") { color = KColors.GRAY }
                    text("$${player.teamPlayer.balance}") { color = KColors.DARKAQUA }
                }
            }
        }
    }
}