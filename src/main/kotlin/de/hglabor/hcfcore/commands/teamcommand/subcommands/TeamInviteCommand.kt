package de.hglabor.hcfcore.commands.teamcommand.subcommands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.commands.teamcommand.TeamCommandCategory
import de.hglabor.hcfcore.commands.teamcommand.ITeamCommand
import de.hglabor.hcfcore.configuration.TConfig
import de.hglabor.hcfcore.manager.player.teamPlayer
import de.hglabor.hcfcore.manager.player.teamPlayerByName
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.commands.argument
import net.axay.kspigot.commands.runs
import net.axay.kspigot.commands.suggestListSuspending
import net.axay.kspigot.extensions.onlinePlayers
import net.minecraft.commands.CommandSourceStack

object TeamInviteCommand: ITeamCommand(TeamCommandCategory.CAPTAIN, "invite") {
    override val usage: String = "/t invite <player>"
    override val description: String = "Invite a player to your team"

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

                // Sender has no permission
                if (teamPlayer.teamRole?.hasBasicPermission == false) {
                    player.sendMsg {
                        text("Only team captains can do this!") { color = KColors.RED }
                    }
                    return@runs
                }

                // Team is full
                if (team.members.size >= TConfig.PLAYERS_PER_FACTION) {
                    player.sendMsg {
                        text("Your team is already full.") { color = KColors.RED }
                    }
                    return@runs
                }

                val targetName = getArgument<String>("player")
                val targetTeamPlayer = teamPlayerByName(targetName)

                // Invited never joined the server
                if (targetTeamPlayer == null) {
                    player.sendMsg {
                        text("$targetName has never joined the server.") { color = KColors.RED }
                    }
                    return@runs
                }

                // Player already in team
                if (team.name == targetTeamPlayer.teamName) {
                    player.sendMsg {
                        text("That player is already on your team.") { color = KColors.RED }
                    }
                    return@runs
                }

                // Player already invited
                if (team.name in targetTeamPlayer.invitesToTeam) {
                    player.sendMsg {
                        text("That player has already been invited.") { color = KColors.RED }
                    }
                    return@runs
                }

                team.invite(player, targetTeamPlayer)
            }
        }

        runs {
            player.sendMsg {
                text("Usage: $usage") { color = KColors.RED }
            }
        }
    }
}