package de.hglabor.hcfcore.commands.teamcommand.subcommands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.commands.teamcommand.TeamCommandCategory
import de.hglabor.hcfcore.commands.teamcommand.ITeamCommand
import de.hglabor.hcfcore.manager.player.teamPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.commands.runs
import net.minecraft.commands.CommandSourceStack

object TeamLeaveCommand: ITeamCommand(TeamCommandCategory.GENERAL, "leave") {
    override val usage: String = "/t leave"
    override val description: String = "Leave your current team"

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

            // Player is on teams claim
            if (Core.claimManager.getClaimAt(player.location)?.name == teamPlayer.teamName) {
                player.sendMsg {
                    text("You cannot leave your team while on team territory.") { color = KColors.RED }
                }
                return@runs
            }

            team.leave(teamPlayer)
        }
    }
}