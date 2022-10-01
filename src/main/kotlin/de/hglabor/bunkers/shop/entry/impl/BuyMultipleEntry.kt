package de.hglabor.bunkers.shop.entry.impl

import de.hglabor.bunkers.shop.entry.IShopEntry
import de.hglabor.bunkers.shop.entry.RightClickAction
import de.hglabor.common.extension.addOrDropItem
import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.player.impl.TeamPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.items.flag
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.axay.kspigot.items.setLore
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import kotlin.math.min

class BuyMultipleEntry(
    override val entryName: String,
    override val item: ItemStack,
    override val price: Int,
) : IShopEntry {

    override val rightClickAction: RightClickAction = RightClickAction(
        if (item.maxStackSize == 1) literalText {
            text("Right Click ") { color = KColors.LIGHTGRAY }
            text("to ") { color = KColors.GRAY }
            text("fill your inventory") { color = KColors.FLORALWHITE }
            italic = false
        }

        else literalText {
            text("Right Click ") { color = KColors.LIGHTGRAY }
            text("to purchase ") { color = KColors.GRAY }
            text("${item.maxStackSize}") { color = KColors.FLORALWHITE }
            italic = false
        }
    ) { player ->
        val bukkitPlayer = player.player ?: return@RightClickAction
        val maxWithBalance = player.balance / price
        if (maxWithBalance == 0) {
            player.sendMsg {
                text("You cannot afford to buy this item!") { color = KColors.RED }
            }
            return@RightClickAction
        }
        val canFit = if (item.maxStackSize == 1) bukkitPlayer.inventory.count { item ->
            item == null || item.type == Material.AIR
        } else item.maxStackSize
        broadcast(item.maxStackSize.toString())
        buy(player, min(maxWithBalance, canFit))
    }

    override fun generateDisplayItem(player: TeamPlayer): ItemStack {
        return item.clone().apply {
            meta {
                name = literalText(entryName) { color = if (player.balance >= price) KColors.GREEN else KColors.RED; italic = false }

                setLore {
                    +literalText("                    ") { color = KColors.DARKGRAY; strikethrough = true }
                    +literalText {
                        text("Price: ") { color = KColors.GRAY }
                        text("$$price") { color = KColors.FLORALWHITE }
                        italic = false
                    }
                    +literalText {
                        text("Left Click ") { color = KColors.LIGHTGRAY }
                        text("to purchase ") { color = KColors.GRAY }
                        text("${amount}") { color = KColors.FLORALWHITE }
                        italic = false
                    }
                    +literalText {
                        component(rightClickAction.description) { italic = false }
                    }
                    +literalText("                    ") { color = KColors.DARKGRAY; strikethrough = true }
                }
                flag(ItemFlag.HIDE_ATTRIBUTES)
            }
        }
    }

    override fun perform(player: TeamPlayer) {
        buy(player, 1)
    }

    private fun buy(player: TeamPlayer, amount: Int) {
        val totalPrice = amount * price
        if (player.balance < totalPrice) {
            player.sendMsg {
                text("You cannot afford to buy this item!") { color = KColors.RED }
            }
            return
        }

        player.player?.let { p ->
            repeat(amount) {
                p.inventory.addOrDropItem(item)
            }
            p.sendMsg {
                text("You have bought ") { color = KColors.GRAY }
                text("${amount}x $entryName ") { color = KColors.AQUAMARINE }
                text("for ") { color = KColors.GRAY }
                text("$$totalPrice") { color = KColors.DARKAQUA }
                text(".") { color = KColors.GRAY }
            }
        }
        player.balance -= totalPrice
    }
}