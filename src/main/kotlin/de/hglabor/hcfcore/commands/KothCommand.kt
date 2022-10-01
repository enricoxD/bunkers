package de.hglabor.hcfcore.commands

import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.event.koth.KothManager
import de.hglabor.hcfcore.team.claim.claimwand.ClaimWandManager
import de.hglabor.hcfcore.team.claim.selection.impl.KothClaimSelection
import de.hglabor.hcfcore.team.impl.KothTeam
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.commands.*

object KothCommand {
    fun register() {
        command("koth") {
            literal("create") {
                argument<String>("name") {
                    suggestSingle { "Name of the koth" }
                    runs {
                        val kothName = getArgument<String>("name")
                        val selection = KothClaimSelection(player.uniqueId, kothName)
                        Core.claimManager.claimSelectionManager.claimSelections[player.uniqueId] = selection
                        ClaimWandManager.giveClaimwand(player)
                    }
                }
            }

            literal("start") {
                argument<String>("koth") {
                    suggestListSuspending { Core.teamManager.teams.filterIsInstance<KothTeam>().map { it.name } }
                    runs {
                        val kothName = getArgument<String>("koth")
                        val koth = Core.teamManager.teamByName(kothName) as? KothTeam

                        if (koth == null) {
                            player.sendMessage(literalText {
                                component(KothManager.kothPrefix)
                                text("No koth with the name $kothName found.") { color = KColors.RED }
                            })
                            return@runs
                        }

                        KothManager.startKoth(koth)
                    }
                }

                runs {
                    val koth = Core.teamManager.cache.values.filterIsInstance<KothTeam>().randomOrNull()
                    if (koth == null) {
                        player.sendMessage(literalText {
                            component(KothManager.kothPrefix)
                            text("No koth found.") { color = KColors.RED }
                        })
                        return@runs
                    }

                    KothManager.startKoth(koth)
                }
            }

            runs {
                val currentKoth = KothManager.currentKoth
                if (currentKoth == null) {
                    player.sendMessage(literalText {
                        component(KothManager.kothPrefix)
                        text("Currently there is no running KOTH.") { color = KColors.RED }
                    })
                    return@runs
                }

                currentKoth.team.sendInfo(player)
            }
        }
    }
}