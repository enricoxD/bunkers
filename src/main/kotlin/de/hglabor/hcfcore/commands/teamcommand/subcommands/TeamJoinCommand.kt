package de.hglabor.hcfcore.commands.teamcommand.subcommands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.commands.teamcommand.TeamCommandCategory
import de.hglabor.hcfcore.commands.teamcommand.ITeamCommand
import de.hglabor.hcfcore.configuration.TConfig
import de.hglabor.hcfcore.team.impl.PlayerTeam
import de.hglabor.hcfcore.manager.player.teamPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.commands.argument
import net.axay.kspigot.commands.runs
import net.axay.kspigot.commands.suggestListSuspending
import net.minecraft.commands.CommandSourceStack

object TeamJoinCommand: ITeamCommand(TeamCommandCategory.GENERAL, "join", "accept") {
    override val usage: String = "/t join <teamName>"
    override val description: String = "Join a team"

    override val commandCallback: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit = {
        argument<String>("team") {
            suggestListSuspending { Core.teamManager.cache.map { it.key } }
            runs {
                val teamPlayer = player.teamPlayer

                // Player already in team
                if (teamPlayer.team != null) {
                    player.sendMsg {
                        text("You are already on a team!") { color = KColors.RED }
                    }
                    return@runs
                }

                val teamName = getArgument<String>("team")
                val team = Core.teamManager.teamByName(teamName)

                // Team doesnt't exist
                if (team == null) {
                    player.sendMsg {
                        text("No team with the name $teamName found.") { color = KColors.RED }
                    }
                    return@runs
                }

                // Player wasn't invited
                if (team.name !in teamPlayer.invitesToTeam || team !is PlayerTeam) {
                    player.sendMsg {
                        text("This team has not invited you!") { color = KColors.RED }
                    }
                    return@runs
                }

                // Team is full
                if (team.members.size >= TConfig.PLAYERS_PER_FACTION) {
                    player.sendMsg {
                        text("This team is already full!") { color = KColors.RED }
                    }
                    return@runs
                }

                // Success
                (team as? PlayerTeam)?.join(teamPlayer)
            }
        }

        runs {
            player.sendMsg {
                text("Usage: $usage") { color = KColors.RED }
            }
        }
    }
}