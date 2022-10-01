package de.hglabor.hcfcore.commands.teamcommand.subcommands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.commands.teamcommand.TeamCommandCategory
import de.hglabor.hcfcore.commands.teamcommand.ITeamCommand
import de.hglabor.hcfcore.manager.player.teamPlayerByName
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.commands.argument
import net.axay.kspigot.commands.runs
import net.minecraft.commands.CommandSourceStack

object TeamWhoCommand: ITeamCommand(TeamCommandCategory.INFORMATION, "who") {
    override val usage: String = "/t who <player | teamName>"
    override val description: String = "Display team information"

    override val commandCallback: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit = {
        argument<String>("name") {
            runs {
                val query = getArgument<String>("name")

                val teamByName = Core.teamManager.teamByName(query)
                    ?: Core.teamManager.cache.values.firstOrNull { it.name.startsWith(query, true) }

                val teamByPlayer = teamPlayerByName(query)?.team

                if (teamByName == null && teamByPlayer == null) {
                    player.sendMsg {
                        text("No team or member with the name $query found.") { color = KColors.RED }
                    }
                    return@runs
                }

                teamByPlayer?.sendInfo(player)
                teamByName?.sendInfo(player)
            }
        }

        runs {
            player.sendMsg {
                text("Usage: /team who <name>") { color = KColors.RED }
            }
        }
    }
}