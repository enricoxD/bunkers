package de.hglabor.hcfcore.commands.teamcommand.subcommands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.commands.teamcommand.TeamCommandCategory
import de.hglabor.hcfcore.commands.teamcommand.ITeamCommand
import de.hglabor.hcfcore.team.data.TeamRole
import de.hglabor.hcfcore.team.impl.PlayerTeam
import de.hglabor.hcfcore.manager.player.teamPlayer
import de.hglabor.hcfcore.manager.player.teamPlayerByName
import de.hglabor.hcfcore.player.impl.TeamPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.commands.argument
import net.axay.kspigot.commands.runs
import net.axay.kspigot.commands.suggestListSuspending
import net.axay.kspigot.extensions.onlinePlayers
import net.minecraft.commands.CommandSourceStack

object TeamKickCommand: ITeamCommand(TeamCommandCategory.CAPTAIN, "kick") {
    override val usage: String = "/t kick <player>"
    override val description: String = "Kick a player from your team"

    override val commandCallback: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit = {
        argument<String>("player") {
            suggestListSuspending { onlinePlayers.map { it.name } }
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

                val targetName = getArgument<String>("player")
                val target = teamPlayerByName(targetName)

                // Invited never joined the server
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

                when (teamPlayer.teamRole) {
                    TeamRole.LEADER -> kick(team, teamPlayer, target)
                    TeamRole.CAPTAIN -> {
                        if (target.teamRole == TeamRole.CAPTAIN || target.teamRole == TeamRole.LEADER) {
                            player.sendMsg {
                                text("You can't kick other Captains or the Leader!") { color = KColors.RED }
                            }
                            return@runs
                        }

                        kick(team, teamPlayer, target)
                    }
                    else -> {
                        player.sendMsg {
                            text("Only team captains can do this!") { color = KColors.RED }
                        }
                    }
                }
            }
        }

        runs {
            player.sendMsg {
                text("Usage: $usage") { color = KColors.RED }
            }
        }
    }

    fun kick(team: PlayerTeam, whoKicked: TeamPlayer, toKick: TeamPlayer) {
        team.notify {
            text("${toKick.name} ") { color = KColors.DARKAQUA }
            text("was ") { color = KColors.GRAY }
            text("kicked ") { color = KColors.RED }
            text("by ") { color = KColors.GRAY }
            text(whoKicked.name) { color = KColors.AQUAMARINE}
            text(".") { color = KColors.GRAY }
        }
        team.claim?.accessibleMembers?.remove(toKick.uuid)
        team.members.remove(toKick.uuid)
    }
}