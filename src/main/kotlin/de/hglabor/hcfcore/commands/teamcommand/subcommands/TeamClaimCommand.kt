package de.hglabor.hcfcore.commands.teamcommand.subcommands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.commands.teamcommand.TeamCommandCategory
import de.hglabor.hcfcore.commands.teamcommand.ITeamCommand
import de.hglabor.hcfcore.team.claim.claimwand.ClaimWandManager
import de.hglabor.hcfcore.team.claim.selection.impl.TeamClaimSelection
import de.hglabor.hcfcore.manager.player.teamPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.commands.runs
import net.minecraft.commands.CommandSourceStack

object TeamClaimCommand: ITeamCommand(TeamCommandCategory.CAPTAIN, "claim") {
    override val usage: String = "/t claim"
    override val description: String = "Start a claim for your team"
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

            val selection = TeamClaimSelection(player.uniqueId, team.name)
            Core.claimManager.claimSelectionManager.claimSelections[player.uniqueId] = selection
            ClaimWandManager.giveClaimwand(player)
        }
    }
}
