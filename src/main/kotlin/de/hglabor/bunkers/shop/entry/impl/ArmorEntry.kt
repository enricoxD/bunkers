package de.hglabor.bunkers.shop.entry.impl

import de.hglabor.bunkers.shop.entry.IShopEntry
import de.hglabor.common.extension.addOrDropItem
import de.hglabor.common.extension.sendMsg
import de.hglabor.common.extension.setOrAdd
import de.hglabor.hcfcore.player.impl.TeamPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.items.flag
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.axay.kspigot.items.setLore
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class ArmorEntry(
    override val entryName: String,
    val equipmentSlot: EquipmentSlot,
    override val item: ItemStack,
    override val price: Int,
) : IShopEntry {
    override val rightClickAction = null

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
                    +literalText("                    ") { color = KColors.DARKGRAY; strikethrough = true }
                }
                flag(ItemFlag.HIDE_ATTRIBUTES)
            }
        }
    }

    override fun perform(player: TeamPlayer) {
        if (player.balance < price) {
            player.sendMsg {
                text("You cannot afford to buy this item!") { color = KColors.RED }
            }
            return
        }


        player.player?.let { p ->
            p.inventory.setOrAdd(equipmentSlot, item)
            p.updateInventory()
            p.sendMsg {
                text("You have bought ") { color = KColors.GRAY }
                text("${item.amount}x $entryName ") { color = KColors.AQUAMARINE }
                text("for ") { color = KColors.GRAY }
                text("$$price") { color = KColors.DARKAQUA }
                text(".") { color = KColors.GRAY }
            }
            player.balance -= price
        }
    }
}