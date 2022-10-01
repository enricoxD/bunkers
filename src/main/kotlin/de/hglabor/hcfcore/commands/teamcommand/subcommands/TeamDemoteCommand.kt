package de.hglabor.hcfcore.commands.teamcommand.subcommands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.commands.teamcommand.TeamCommandCategory
import de.hglabor.hcfcore.commands.teamcommand.ITeamCommand
import de.hglabor.hcfcore.team.data.TeamRole
import de.hglabor.hcfcore.manager.player.teamPlayer
import de.hglabor.hcfcore.manager.player.teamPlayerByName
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.commands.argument
import net.axay.kspigot.commands.runs
import net.axay.kspigot.commands.suggestListSuspending
import net.axay.kspigot.extensions.onlinePlayers
import net.minecraft.commands.CommandSourceStack

object TeamDemoteCommand: ITeamCommand(TeamCommandCategory.LEADER, "demote") {
    override val usage: String = "/t demote <player>"
    override val description: String = "Demote a player to a member"

    override val commandCallback: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit = {
        argument<String>("member") {
            suggestListSuspending { onlinePlayers.map { it.name } }
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
                if (teamPlayer.teamRole?.hasFullPermission == false) {
                    player.sendMsg {
                        text("Only team leaders can do this!") { color = KColors.RED }
                    }
                    return@runs
                }

                val targetName = getArgument<String>("member")
                val target = teamPlayerByName(targetName)

                // Target hasn't played
                if (target == null) {
                    player.sendMsg {
                        text("$targetName has never joined the server.") { color = KColors.RED }
                    }
                    return@runs
                }

                // Mismatching teams
                if (target.team != team) {
                    player.sendMsg {
                        text("${target.name} is not on your team!") { color = KColors.RED }
                    }
                    return@runs
                }

                // Sender is target
                if (target.uuid == player.uniqueId) {
                    player.sendMsg {
                        text("You cannot demote yourself!") { color = KColors.RED }
                    }
                    return@runs
                }

                // Target is member
                if (target.teamRole == TeamRole.MEMBER) {
                    player.sendMsg {
                        text("${target.name} is currently a member! To kick them a, use /team kick") {
                            color = KColors.RED
                        }
                    }
                    return@runs
                }

                target.teamRole = TeamRole.MEMBER
                team.notify {
                    text("${target.name} ") { color = KColors.AQUAMARINE }
                    text("has been ") { color = KColors.GRAY }
                    text("demoted ") { color = KColors.LIME }
                    text("to a ") { color = KColors.GRAY }
                    text("member ") { color = KColors.DARKAQUA }
                    text("!") { color = KColors.GRAY }
                }
            }
        }

        runs {
            player.sendMsg {
                text("Usage: $usage") { color = KColors.RED }
            }
        }
    }
}
