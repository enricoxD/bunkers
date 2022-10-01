package de.hglabor.hcfcore.commands.teamcommand.subcommands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.commands.teamcommand.TeamCommandCategory
import de.hglabor.hcfcore.commands.teamcommand.ITeamCommand
import de.hglabor.hcfcore.manager.player.teamPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.commands.runs
import net.minecraft.commands.CommandSourceStack

object TeamDisbandCommand: ITeamCommand(TeamCommandCategory.LEADER, "disband") {
    override val usage: String = "/t disband"
    override val description: String = "Disband your team"

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
            if (teamPlayer.teamRole?.hasFullPermission == false) {
                player.sendMsg {
                    text("Only team leaders can do this!") { color = KColors.RED }
                }
                return@runs
            }

            team.disband()
        }
    }
}
