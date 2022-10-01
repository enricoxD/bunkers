package de.hglabor.hcfcore.commands.teamcommand.subcommands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.commands.teamcommand.TeamCommandCategory
import de.hglabor.hcfcore.commands.teamcommand.ITeamCommand
import de.hglabor.hcfcore.manager.player.teamPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.commands.runs
import net.minecraft.commands.CommandSourceStack

object TeamSetHomeCommand: ITeamCommand(TeamCommandCategory.CAPTAIN, "sethome", "sethq") {
    override val usage: String = "/t sethome"
    override val description: String = "Set your team's home at your current location"

    override val commandCallback: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit = {
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

            val loc = player.location
            if (team.claim?.region?.isInArea(loc, false) == false) {
                player.sendMsg {
                    text("You can only set a HQ in your team's territory.") { color = KColors.RED }
                }
                return@runs
            }
            team.setHome(teamPlayer)
        }
    }
}
