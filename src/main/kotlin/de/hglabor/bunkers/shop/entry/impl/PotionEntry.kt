package de.hglabor.bunkers.shop.entry.impl

import de.hglabor.bunkers.shop.entry.IShopEntry
import de.hglabor.bunkers.shop.entry.RightClickAction
import de.hglabor.common.extension.addOrDropItem
import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.player.impl.TeamPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.items.*
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionData

class PotionEntry(
    override val entryName: String,
    val potionData: PotionData,
    splash: Boolean,
    override val price: Int,
    val description: Component? = null
    ) : IShopEntry {
    override val item: ItemStack = itemStack(if (splash) Material.SPLASH_POTION else Material.POTION) {
        meta<PotionMeta> { basePotionData = potionData }
    }
    override val rightClickAction: RightClickAction? = null

    override fun generateDisplayItem(player: TeamPlayer): ItemStack {
        return item.clone().apply {
            meta<PotionMeta> {
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
                    if (description != null) {
                        +literalText("                    ") { color = KColors.DARKGRAY; strikethrough = true }
                        +literalText {
                            component(description) { italic = false }
                        }
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
            p.inventory.addOrDropItem(item)
            p.sendMsg {
                text("You have bought ") { color = KColors.GRAY }
                text("${item.amount}x $entryName ") { color = KColors.AQUAMARINE }
                text("for ") { color = KColors.GRAY }
                text("$$price") { color = KColors.DARKAQUA }
                text(".") { color = KColors.GRAY }
            }
        }
        player.balance -= price
    }
}