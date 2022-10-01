package de.hglabor.hcfcore.commands.hcfcorecommand.subcommands

import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.commands.hcfcorecommand.hcfCoreCommand
import de.hglabor.hcfcore.team.claim.claimwand.ClaimWandManager
import de.hglabor.hcfcore.team.claim.impl.WarzoneClaim
import de.hglabor.hcfcore.team.claim.selection.impl.SpawnClaimSelection
import de.hglabor.hcfcore.team.impl.WarzoneTeam
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.commands.argument
import net.axay.kspigot.commands.literal
import net.axay.kspigot.commands.runs
import net.axay.kspigot.commands.suggestSingle

object HCFCoreClaimCommand {
    val cmd = hcfCoreCommand("claim") {
        literal("spawn") {
            argument<String>("name") {
                suggestSingle { "Name of the Spawn" }
                runs {
                    val spawnName = getArgument<String>("name")
                    val selection = SpawnClaimSelection(player.uniqueId, spawnName)
                    Core.claimManager.claimSelectionManager.claimSelections[player.uniqueId] = selection
                    ClaimWandManager.giveClaimwand(player)
                }
            }
        }

        literal("warzone") {
            argument<String>("name") {
                suggestSingle { "The name of the Warzone" }
                argument<Int>("radius") {
                    suggestSingle { "The radius of the Warzone" }
                    runs {
                        val name = getArgument<String>("name")
                        val radius = getArgument<Int>("radius")

                        if(Core.teamManager.teams.filterIsInstance<WarzoneTeam>().any { it.claim.world == player.world }) {
                            player.sendMsg {
                                text("You cannot create another Warzone in your current world.") { color = KColors.RED }
                            }
                            return@runs
                        }

                        val claim = WarzoneClaim(player.world.name, radius, name)
                        Core.claimManager.claim(claim)
                        Core.teamManager.cache[name] = WarzoneTeam(name, claim)
                        player.sendMsg {
                            text("Successfully ") { color = KColors.LIME }
                            text("created a warzone with the name ") { color = KColors.GRAY }
                            text(name) { color = KColors.RED }
                            text(".") { color = KColors.GRAY }
                        }
                    }
                }
            }
        }
    }
}