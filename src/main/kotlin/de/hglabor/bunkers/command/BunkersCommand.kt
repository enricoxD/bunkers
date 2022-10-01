package de.hglabor.bunkers.command

import de.hglabor.bunkers.creation.BunkersClaimSelection
import de.hglabor.bunkers.shop.ShopManager
import de.hglabor.bunkers.shop.shops.BuildShop
import de.hglabor.bunkers.shop.shops.CombatShop
import de.hglabor.bunkers.shop.shops.EnchantmentShop
import de.hglabor.bunkers.shop.shops.SellShop
import de.hglabor.bunkers.teams.BunkersTeam
import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.team.claim.claimwand.ClaimWandManager
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.commands.*
import net.axay.kspigot.utils.CardinalDirection
import kotlin.reflect.full.primaryConstructor

object BunkersCommand {
    fun register() {
        command("bunkers") {
            requiresPermission("hglabor.admin")
            literal("teams") {
                literal("sethome") {
                    runs {
                        val team = Core.claimManager.getTeamAt(player.location) as? BunkersTeam

                        if (team == null) {
                            player.sendMsg {
                                text("You can only set a HQ within a claim.") { color = KColors.RED }
                            }
                            return@runs
                        }

                        val playerLocation = player.location.toCenterLocation().add(0.0, -0.5, 0.0)
                        val direction = CardinalDirection.fromLocation(playerLocation).facing!!.direction
                        playerLocation.direction = direction

                        team.homeLocation = playerLocation
                        player.sendMsg {
                            text("You updated the ") { color = KColors.GRAY }
                            text("${team.name} ") { color = team.teamColor }
                            text("team's ") { color = KColors.GRAY }
                            text("HQ point") { color = KColors.DARKAQUA }
                            text("!") { color = KColors.GRAY }
                        }
                    }
                }

                literal("setshop") {
                    argument<String>("shop") {
                        suggestListSuspending { listOf("build", "combat", "sell", "enchant") }
                        runs {
                            kotlin.runCatching {
                                val team = Core.claimManager.getTeamAt(player.location) as? BunkersTeam

                                if (team == null) {
                                    player.sendMsg {
                                        text("You can only create a shop within a claim.") { color = KColors.RED }
                                    }
                                    return@runs
                                }

                                val shopName = getArgument<String>("shop")
                                val c = when (shopName.lowercase()) {
                                    "build" -> BuildShop::class
                                    "combat" -> CombatShop::class
                                    "sell" -> SellShop::class
                                    "enchant" -> EnchantmentShop::class
                                    else -> return@runs
                                }

                                val playerLocation = player.location.toCenterLocation().add(0.0, -0.5, 0.0)
                                val direction = CardinalDirection.fromLocation(playerLocation).facing!!.direction
                                playerLocation.direction = direction

                                val shop = c.primaryConstructor?.call(team.name, playerLocation)
                                if (shop == null) {
                                    player.sendMsg {
                                        text("Error creating the shop?") { color = KColors.RED }
                                    }
                                    return@runs
                                }

                                ShopManager.registerShop(shop)
                                ShopManager.spawnVillager(shop)
                                player.sendMsg {
                                    text("You updated the ") { color = KColors.GRAY }
                                    text("${team.name} ") { color = team.teamColor }
                                    text("team's ") { color = KColors.GRAY }
                                    text("$shopName shop") { color = KColors.DARKAQUA }
                                    text("!") { color = KColors.GRAY }
                                }
                            }.onFailure {
                                it.printStackTrace()
                            }
                        }
                    }
                }

                literal("claim") {
                    argument<String>("team") {
                        suggestListSuspending { Core.teamManager.teams.filterIsInstance<BunkersTeam>().map { it.name } }
                        runs {
                            val teamName = getArgument<String>("team")
                            val team = Core.teamManager.teamByName(teamName) as? BunkersTeam

                            if (team == null) {
                                player.sendMsg {
                                    text("No team with the name $teamName found.") { color = KColors.RED }
                                }
                                return@runs
                            }
                            val selection = BunkersClaimSelection(player.uniqueId, team)
                            Core.claimManager.claimSelectionManager.claimSelections[player.uniqueId] = selection
                            ClaimWandManager.giveClaimwand(player)
                        }
                    }
                }
            }

            literal("save") {
                runs {
                    Core.disable()
                    ShopManager.disable()
                }
            }
        }
    }
}