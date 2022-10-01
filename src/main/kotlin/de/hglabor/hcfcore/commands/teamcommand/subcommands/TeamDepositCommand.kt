package de.hglabor.hcfcore.commands.teamcommand.subcommands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.commands.teamcommand.TeamCommandCategory
import de.hglabor.hcfcore.commands.teamcommand.ITeamCommand
import de.hglabor.hcfcore.team.impl.PlayerTeam
import de.hglabor.hcfcore.manager.player.teamPlayer
import de.hglabor.hcfcore.player.impl.TeamPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.commands.argument
import net.axay.kspigot.commands.literal
import net.axay.kspigot.commands.runs
import net.minecraft.commands.CommandSourceStack

object TeamDepositCommand: ITeamCommand(TeamCommandCategory.GENERAL, "deposit", "d") {
    override val usage: String = "/t deposit <amount | all>"
    override val description: String = "Deposit money into your team balance"

    override val commandCallback: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit = {
        argument<Int>("amount") {
            runs {
                val teamPlayer = player.teamPlayer
                val team = teamPlayer.team

                // Player not on a team
                if (team == null) {
                    player.sendMsg {
                        text("You are not on a team!") { color = KColors.RED }
                    }
                    return@runs
                }

                val amount = getArgument<Int>("amount")
                // player does not have enough money
                if (teamPlayer.balance < amount) {
                    player.sendMsg {
                        text("You don't have enough money to do this!") { color = KColors.RED }
                    }
                    return@runs
                }

                // Success
                deposit(team, teamPlayer, amount)
            }
        }

        literal("all") {
            runs {
                val teamPlayer = player.teamPlayer
                val team = teamPlayer.team

                // Player not on a team
                if (team == null) {
                    player.sendMsg {
                        text("You are not on a team!") { color = KColors.RED }
                    }
                    return@runs
                }

                // player does not have enough money
                if (teamPlayer.balance <= 0) {
                    player.sendMsg {
                        text("You don't have enough money to do this!") { color = KColors.RED }
                    }
                    return@runs
                }

                // Success
                deposit(team, teamPlayer, teamPlayer.balance)
            }
        }

        runs {
            player.sendMsg {
                text("Usage: $usage") { color = KColors.RED }
            }
        }
    }

    private fun deposit(team: PlayerTeam, teamPlayer: TeamPlayer, amount: Int) {
        teamPlayer.sendMsg {
            text("You have added ") { color = KColors.GRAY }
            text("$$amount ") { color = KColors.DARKAQUA }
            text("to the team balance!") { color = KColors.GRAY }
        }
        team.notify {
            text("${teamPlayer.name} ") { color = KColors.AQUAMARINE }
            text("deposited ") { color = KColors.GRAY }
            text("$$amount ") { color = KColors.DARKAQUA }
            text("into the team balance.") { color = KColors.GRAY }
        }

        teamPlayer.balance -= amount
        team.balance += amount
    }
}