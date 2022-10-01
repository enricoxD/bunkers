package de.hglabor.hcfcore.team.claim.claimwand

import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.configuration.TConfig
import de.hglabor.hcfcore.team.claim.selection.ClaimSelectionManager
import de.hglabor.hcfcore.team.claim.selection.OverlappingClaimSelection
import de.hglabor.hcfcore.team.claim.selection.impl.KothClaimSelection
import de.hglabor.hcfcore.team.claim.selection.impl.TeamClaimSelection
import de.hglabor.hcfcore.visualization.VisualizationHandler
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.event.listen
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import kotlin.math.abs

object ClaimWandListener {
    fun enable() {
        listen<PlayerInteractEvent>(ignoreCancelled = false, priority = EventPriority.HIGH) {
            if (it.hand == EquipmentSlot.OFF_HAND) return@listen
            val action = it.action
            val player = it.player

            // Player is not using a claim-wand
            if (action == Action.PHYSICAL || it.item != ClaimWandManager.claimWandItem) {
                return@listen
            }

            if (action.isRightClick) {
                it.isCancelled = true
            }

            val selection = Core.claimManager.claimSelectionManager.getSelection(player.uniqueId)
            if (selection == null) {
                player.sendMsg {
                    text("Removed your claim wand because you do not have an active selection!") { color = KColors.RED }
                    newLine()
                    text("Please try again.") { color = KColors.RED }
                }
                return@listen
            }

            // Confirm Claim
            if (player.isSneaking && action.isLeftClick) {
                val region = selection.asRegion()
                val price = Core.claimManager.calculatePrice(selection, false)

                // Player hasn't selected both corners
                if (region == null || price <= 0) {
                    player.sendMsg {
                        text("You have not selected both corners of your claim yet!") { color = KColors.RED }
                    }
                    return@listen
                }

                // Overlapping claims
                if (selection !is OverlappingClaimSelection && Core.claimManager.hasOverlappingClaims(selection)) {
                    player.sendMsg {
                        text("You can only claim land in the Wilderness!") { color = KColors.RED }
                        newLine()
                        text("Note: You need to have a 5 block distance to other claims.") { color = KColors.RED }
                    }
                    return@listen
                }

                selection.confirm()
                return@listen
            }

            // Clearing the ClaimSelection
            if (action == Action.RIGHT_CLICK_AIR) {
                Core.claimManager.claimSelectionManager.clearSelection(player.uniqueId)
                return@listen
            }

            // Set the claim corners
            if (action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK) {
                val clickedBlock = it.clickedBlock ?: return@listen
                val clickedLocation = clickedBlock.location.add(0.5, 0.0, 0.5)

                if (System.currentTimeMillis() - selection.lastUpdate < 2) {
                    return@listen
                }

                if (selection !is OverlappingClaimSelection && Core.claimManager.getClaimAt(clickedLocation) != null) {
                    player.sendMsg {
                        text("You can only claim land in the Wilderness!") { color = KColors.RED }
                        newLine()
                        text("Note: You need to have a 5 block distance to other claims.") { color = KColors.RED }
                    }
                    return@listen
                }

                val locationId: Int
                when (action) {
                    Action.LEFT_CLICK_BLOCK -> {
                        val oldLocation = selection.loc1
                        if (isSameLocation(oldLocation, clickedLocation)) return@listen
                        if (selectionIsTooSmall(player, clickedLocation, selection.loc2)) return@listen
                        selection.loc1 = clickedLocation
                        locationId = 1
                    }

                    Action.RIGHT_CLICK_BLOCK -> {
                        val oldLocation = selection.loc2
                        if (isSameLocation(oldLocation, clickedLocation)) return@listen
                        if (selectionIsTooSmall(player, clickedLocation, selection.loc1)) return@listen
                        selection.loc2 = clickedLocation
                        locationId = 2
                    }

                    else -> error("Unexpected action!")
                }

                player.sendMsg {
                    text("Set claim's location ") { color = KColors.GRAY }
                    text("$locationId ") { color = KColors.AQUAMARINE }
                    text("to ") { color = KColors.GRAY }
                    text("(") { color = KColors.DARKGRAY }
                    text("${clickedLocation.x.toInt()}") { color = KColors.FLORALWHITE }
                    text(", ") { color = KColors.DARKGRAY }
                    text("${clickedLocation.z.toInt()}") { color = KColors.FLORALWHITE }
                    text(")") { color = KColors.DARKGRAY }
                }

                VisualizationHandler.showSelectionCorners(player, Material.RED_STAINED_GLASS, selection)

                val opposite = if (locationId == 1) selection.loc2 else selection.loc1
                val region = selection.asRegion() ?: return@listen
                if (opposite != null) {
                    val size = literalText {
                        text("Current size: (") { color = KColors.GRAY }
                        text("${selection.xDistance()}, ${selection.zDistance()}") { color = KColors.FLORALWHITE }
                        text("), ") { color = KColors.GRAY }
                        text("${selection.totalBlocks()} ") { color = KColors.FLORALWHITE }
                        text("Blocks") { color = KColors.GRAY }
                    }

                    if (selection is TeamClaimSelection) {
                        val price = Core.claimManager.calculatePrice(region, false)
                        player.sendMsg {
                            text("Claim cost: ") { color = KColors.GRAY }
                            text("$$price") { color = KColors.DARKAQUA }
                            text(", ") { color = KColors.GRAY }
                            component(size)
                        }
                    } else {
                        player.sendMsg {
                            component(size)
                        }
                    }
                }
            }
        }

        listen<PlayerItemDamageEvent>(ignoreCancelled = false, priority = EventPriority.HIGH) {
            if (it.item == ClaimWandManager.claimWandItem)
                it.isCancelled = true
        }

        listen<BlockBreakEvent>(ignoreCancelled = false, priority = EventPriority.HIGH) {
            if (it.player.inventory.itemInMainHand == ClaimWandManager.claimWandItem)
                it.isCancelled = true
        }

        listen<PlayerDropItemEvent>(ignoreCancelled = false, priority = EventPriority.HIGH) {
            if (it.itemDrop.itemStack == ClaimWandManager.claimWandItem) {
                it.itemDrop.itemStack = ItemStack(Material.AIR)
                Core.claimManager.claimSelectionManager.clearSelection(it.player.uniqueId)
            }
        }

        listen<PlayerQuitEvent>(ignoreCancelled = false, priority = EventPriority.HIGH) {
            Core.claimManager.claimSelectionManager.clearSelection(it.player.uniqueId)
        }
    }

    private fun selectionIsTooSmall(player: Player, corner: Location, opposite: Location?): Boolean {
        if (opposite != null) {
            if (abs(opposite.blockX - corner.blockX) < TConfig.MIN_CLAIM_RADIUS ||
                abs(opposite.blockZ - corner.blockZ) < TConfig.MIN_CLAIM_RADIUS
            ) {
                val selection = ClaimSelectionManager.getSelection(player.uniqueId)
                if (selection is KothClaimSelection) {
                    if (selection.teamRegion != null) {
                        return false
                    }
                }
                player.sendMsg {
                    text("Your claim is too small! The claim has to be at least 5 x 5!") { color = KColors.RED }
                }
                return true
            }
        }
        return false
    }

    private fun isSameLocation(loc1: Location?, loc2: Location): Boolean {
        if (loc1 == null) return false
        if (loc1.blockX != loc2.blockX) return false
        return loc2.blockZ == loc2.blockZ
    }
}