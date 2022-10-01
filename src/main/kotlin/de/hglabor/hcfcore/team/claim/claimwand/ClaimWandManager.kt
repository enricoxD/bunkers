package de.hglabor.hcfcore.team.claim.claimwand

import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.axay.kspigot.items.setLore
import org.bukkit.Material
import org.bukkit.entity.Player

object ClaimWandManager {
    val claimWandItem = itemStack(Material.WOODEN_HOE) {
        meta {
            name = literalText("Claiming Wand") { color = KColors.LIME }

            setLore {
                +literalText {
                    text("Right/Left Click ") { color = KColors.GRAY; italic = false }
                    text("Block") { color = KColors.AQUAMARINE; italic = false }
                }
                +literalText {
                    text("- ") { color = KColors.DARKGRAY; italic = false }
                    text("Select claim's corners") { color = KColors.FLORALWHITE; italic = false }
                }
                +""
                +literalText {
                    text("Right Click ") { color = KColors.GRAY; italic = false }
                    text("Air") { color = KColors.AQUAMARINE; italic = false }
                }
                +literalText {
                    text("- ") { color = KColors.DARKGRAY; italic = false }
                    text("Cancel current claim") { color = KColors.FLORALWHITE; italic = false }
                }
                +""
                +literalText {
                    text("Crouch ") { color = KColors.DARKAQUA; italic = false }
                    text("Left Click ") { color = KColors.GRAY; italic = false }
                    text("Block/Air") { color = KColors.AQUAMARINE; italic = false }
                }
                +literalText {
                    text("- ") { color = KColors.DARKGRAY; italic = false }
                    text("Purchase current claim") { color = KColors.FLORALWHITE; italic = false }
                }
            }
        }
    }

    fun giveClaimwand(player: Player) {
        player.sendMessage(literalText {
            text("Land claim started.") { color = KColors.AQUAMARINE }
            newLine()
            text("Left click at a corner of the land you'd like to claim.") { color = KColors.GRAY }
            newLine()
            text("Right click on the second corner of the land you'd like to claim.") { color = KColors.GRAY }
            newLine()
            text("Crouch left click to purchase your claim.") { color = KColors.GRAY }
            newLine()
            text("Gave you a claim wand.") { color = KColors.LIME }
        })
        player.inventory.addItem(claimWandItem)
    }

    fun enable() {
        ClaimWandListener.enable()
    }
}