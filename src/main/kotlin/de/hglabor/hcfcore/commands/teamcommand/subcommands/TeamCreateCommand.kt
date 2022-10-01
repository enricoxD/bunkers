package de.hglabor.hcfcore.commands.teamcommand.subcommands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import de.hglabor.common.extension.broadcast
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

object TeamCreateCommand: ITeamCommand(TeamCommandCategory.GENERAL, "create") {
    override val usage: String = "/t create <teamName>"
    override val description: String = "Create a new team"

    override val commandCallback: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit = {
        argument<String>("name") {
            runs {
                kotlin.runCatching {
                    val teamPlayer = player.teamPlayer
                    if (teamPlayer.team != null) {
                        player.sendMsg {
                            text("You are already on a team!") { color = KColors.RED }
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

                    Core.teamManager.createTeam(player, teamName)

                    broadcast {
                        text("Team "){ color = KColors.GRAY }
                        text("$teamName ") { color = KColors.AQUAMARINE }
                        text("has been ") { color = KColors.GRAY }
                        text("created ") { color = KColors.LIME }
                        text("by ") { color = KColors.GRAY }
                        text("${player.name}") { color = KColors.DARKAQUA }
                        text("!") { color = KColors.GRAY }
                    }

                    player.sendMsg {
                        text("To learn more about teams, do /team") { color = KColors.GRAY }
                    }
                }.onFailure {
                    it.printStackTrace()
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