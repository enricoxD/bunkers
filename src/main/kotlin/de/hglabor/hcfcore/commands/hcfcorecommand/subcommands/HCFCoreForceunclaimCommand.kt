package de.hglabor.hcfcore.commands.hcfcorecommand.subcommands

import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.commands.hcfcorecommand.hcfCoreCommand
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.commands.runs

object HCFCoreForceunclaimCommand {
    val cmd = hcfCoreCommand("forceunclaim") {
        runs {
            val claim = Core.claimManager.getClaimAt(player.location)
            if (claim == null) {
                player.sendMsg {
                    text("There is no claim at your location!") { color = KColors.RED }
                }
                return@runs
            }

            player.sendMsg {
                text("Successfully unclaimed ${claim.name}'s land!") { color = KColors.LIME }
            }
            claim.unclaim()
        }
    }
}