package de.hglabor.hcfcore.commands.teamcommand.subcommands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.commands.teamcommand.TeamCommandCategory
import de.hglabor.hcfcore.commands.teamcommand.ITeamCommand
import de.hglabor.hcfcore.configuration.TConfig
import de.hglabor.hcfcore.manager.player.teamPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.commands.argument
import net.axay.kspigot.commands.runs
import net.minecraft.commands.CommandSourceStack

object TeamRenameCommand: ITeamCommand(TeamCommandCategory.LEADER, "rename") {
    override val usage: String = "/t rename <new name>"
    override val description: String = "Rename your team"

    override val commandCallback: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit = {
        argument<String>("name") {
            runs {
                val teamPlayer = player.teamPlayer
                val team = teamPlayer.team

                // Player not on team
                if (team == null) {
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

                val teamName: String = getArgument("name")

                // Name too long
                if (teamName.length > TConfig.FACTION_MAX_NAME_LENGTH) {
                    player.sendMsg {
                        text("Maximum team size is ${TConfig.FACTION_MAX_NAME_LENGTH} characters!") { color = KColors.RED }
                    }
                    return@runs
                }

                // Name too short
                if (teamName.length < TConfig.FACTION_MIN_NAME_LENGTH) {
                    player.sendMsg {
                        text("Minimum team size is ${TConfig.FACTION_MIN_NAME_LENGTH} characters!") { color = KColors.RED }
                    }
                    return@runs
                }

                // Name exists
                if (!Core.teamManager.isNameFree(teamName)) {
                    player.sendMsg {
                        text("That team already exists!") { color = KColors.RED }
                    }
                    return@runs
                }

                Core.teamManager.renameTeam(team, teamName)
                team.notify {
                    text("${player.name} ") { color = KColors.AQUAMARINE }
                    text("renamed ") { color = KColors.LIME }
                    text("the team to ") { color = KColors.GRAY}
                    text("$teamName") { color = KColors.DARKAQUA }
                    text(".") { color = KColors.GRAY}
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