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

object TeamWithdrawCommand: ITeamCommand(TeamCommandCategory.CAPTAIN, "withdraw", "w") {
    override val usage: String = "/t withdraw <amount | all>"
    override val description: String = "Withdraw money from your team's balance"

    override val commandCallback: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit = {
        argument<Int>("amount") {
            runs {
                val teamPlayer = player.teamPlayer
                val team = teamPlayer.team
                val teamRole = teamPlayer.teamRole

                // Player not on a team
                if (team == null || teamRole == null) {
                    player.sendMsg {
                        text("You are not on a team!") { color = KColors.RED }
                    }
                    return@runs
                }

                // Sender has no permission
                if (teamPlayer.teamRole?.hasBasicPermission == false) {
                    player.sendMsg {
                        text("Only team captains can do this!") { color = KColors.RED }
                    }
                    return@runs
                }

                val amount = getArgument<Int>("amount")
                // team does not have enough money
                if (team.balance < amount) {
                    player.sendMsg {
                        text("Your team doesn't have enough money to do this!") { color = KColors.RED }
                    }
                    return@runs
                }

                withdraw(team, teamPlayer, amount)
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

                // Sender has no permission
                if (teamPlayer.teamRole?.hasBasicPermission == false) {
                    player.sendMsg {
                        text("Only team captains can do this!") { color = KColors.RED }
                    }
                    return@runs
                }

                // team does not have enough money
                if (team.balance <= 0) {
                    player.sendMsg {
                        text("The team doesn't have enough money to do this!") { color = KColors.RED }
                    }
                    return@runs
                }

                // Success
                withdraw(team, teamPlayer, team.balance)
            }
        }

        runs {
            player.sendMsg {
                text("Usage: $usage") { color = KColors.RED }
            }
        }
    }

    private fun withdraw(team: PlayerTeam, teamPlayer: TeamPlayer, amount: Int) {
        teamPlayer.sendMsg {
            text("You have withdrawn ") { color = KColors.GRAY }
            text("$$amount ") { color = KColors.DARKAQUA }
            text("from the team balance!") { color = KColors.GRAY }
        }
        team.notify {
            text("${teamPlayer.name} ") { color = KColors.AQUAMARINE }
            text("withdrew ") { color = KColors.GRAY }
            text("$$amount ") { color = KColors.DARKAQUA }
            text("from the team balance.") { color = KColors.GRAY }
        }

        teamPlayer.balance += amount
        team.balance -= amount
    }
}