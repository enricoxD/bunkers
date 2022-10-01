package de.hglabor.hcfcore.commands.teamcommand.subcommands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.team.claim.impl.TeamClaim
import de.hglabor.hcfcore.commands.teamcommand.TeamCommandCategory
import de.hglabor.hcfcore.commands.teamcommand.ITeamCommand
import de.hglabor.hcfcore.manager.player.teamPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.commands.runs
import net.minecraft.commands.CommandSourceStack

object TeamUnclaimCommand: ITeamCommand(TeamCommandCategory.LEADER, "unclaim") {
    override val usage: String = "/t unclaim"
    override val description: String = "Sell your team's claim"

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

            val claim = Core.claimManager.getClaimAt(player.location)
            // Player is not standing inside it's own claim
            if (claim == null || claim.name != team.name || claim !is TeamClaim) {
                player.sendMsg {
                    text("You can only unclaim your own claim!") { color = KColors.RED }
                }
                return@runs
            }

            val claimPrice = Core.claimManager.calculatePrice(claim.region, true)
            team.balance += claimPrice
            team.notify {
                text("${player.name} ") { color = KColors.AQUAMARINE }
                text("has ") { color = KColors.GRAY }
                text("sold ") { color = KColors.RED }
                text("the team's ") { color = KColors.GRAY }
                text("claim") { color = KColors.DARKAQUA }
                text(".") { color = KColors.GRAY }
            }
            team.notify {
                text("$$claimPrice ") { color = KColors.DARKAQUA }
                text("have been ") { color = KColors.GRAY }
                text("added ") { color = KColors.LIME }
                text("to your team's balance.") { color = KColors.GRAY }
            }
            Core.claimManager.unclaim(claim)
        }
    }
}
