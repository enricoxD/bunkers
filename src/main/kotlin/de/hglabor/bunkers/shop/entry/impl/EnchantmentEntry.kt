package de.hglabor.bunkers.shop.entry.impl

import de.hglabor.bunkers.shop.entry.IShopEntry
import de.hglabor.bunkers.shop.entry.RightClickAction
import de.hglabor.common.extension.addOrDropItem
import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.manager.player.teamPlayer
import de.hglabor.hcfcore.player.impl.TeamPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.items.*
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

open class EnchantmentEntry(
    override val entryName: String,
    val enchantment: Enchantment,
    val enchantmentLevel: Int,
    val description: Component? = null,
    override val price: Int,
    val performCallback: ((Player, EnchantmentEntry) -> Boolean)
) : IShopEntry {
    override val item = itemStack(Material.ENCHANTED_BOOK) {}
    override val rightClickAction: RightClickAction? = null

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
                    if (description != null) {
                        +literalText("                    ") { color = KColors.DARKGRAY; strikethrough = true }
                        +literalText {
                            component(description) { italic = false; color = KColors.GRAY }
                        }
                    }
                    +literalText("                    ") { color = KColors.DARKGRAY; strikethrough = true }
                }
                flag(ItemFlag.HIDE_ATTRIBUTES)
            }
        }
    }

    override fun perform(player: TeamPlayer) {
        val bukkitPlayer = player.player ?: return
        if (player.balance < price) {
            player.sendMsg {
                text("You cannot afford to buy this enchantment!") { color = KColors.RED }
            }
            return
        }

        if (performCallback(bukkitPlayer, this)) {
            player.sendMsg {
                text("You have bought ") { color = KColors.GRAY }
                text("$entryName ") { color = KColors.AQUAMARINE }
                text("for ") { color = KColors.GRAY }
                text("$$price") { color = KColors.DARKAQUA }
                text(".") { color = KColors.GRAY }
            }
            player.balance -= price
        }
    }
}