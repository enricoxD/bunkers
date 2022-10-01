package de.hglabor.bunkers.shop.entry.impl

import de.hglabor.bunkers.shop.entry.IShopEntry
import de.hglabor.bunkers.shop.entry.RightClickAction
import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.player.impl.TeamPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.items.flag
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.axay.kspigot.items.setLore
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class SellEntry(
    override val entryName: String,
    override val item: ItemStack,
    override val price: Int,
) : IShopEntry {
    override val rightClickAction: RightClickAction = RightClickAction(
        literalText {
            text("Right Click ") { color = KColors.LIGHTGRAY }
            text("to sell ") { color = KColors.GRAY }
            text("all") { color = KColors.FLORALWHITE }
        }
    ) { player ->
        val bukkitPlayer = player.player ?: return@RightClickAction
        val type = item.type

        if (!playerHasItem(bukkitPlayer, item.type)) {
            player.sendMsg {
                text("You do not have any $entryName!") { color = KColors.RED }
            }
            bukkitPlayer.playSound(bukkitPlayer.location, Sound.ENTITY_VILLAGER_NO, 1f, 1f)
            return@RightClickAction
        }

        val removed = bukkitPlayer.inventory.all(type).toList().fold(0) { acc, (slot, item) ->
            bukkitPlayer.inventory.setItem(slot, null)
            acc + item.amount
        }
        val totalPrice = removed * price

        bukkitPlayer.sendMsg {
            text("You sold ") { color = KColors.GRAY }
            text("${removed}x $entryName ") { color = KColors.AQUAMARINE }
            text("for ") { color = KColors.GRAY }
            text("$$totalPrice") { color = KColors.DARKAQUA }
            text(".") { color = KColors.GRAY }
        }
        player.balance += totalPrice
    }

    override fun generateDisplayItem(player: TeamPlayer): ItemStack {
        return item.clone().apply {
            meta {
                name = literalText(entryName) {
                    color = if (playerHasItem(player.player, item.type)) KColors.GREEN else KColors.RED
                    italic = false
                }

                setLore {
                    +literalText("                    ") { color = KColors.DARKGRAY; strikethrough = true }
                    +literalText {
                        text("Price: ") { color = KColors.GRAY }
                        text("$$price") { color = KColors.FLORALWHITE }
                        italic = false
                    }
                    +literalText {
                        text("Left Click: ") { color = KColors.LIGHTGRAY }
                        text("To sell ") { color = KColors.GRAY }
                        text("${amount}") { color = KColors.FLORALWHITE }
                        italic = false
                    }
                    +literalText {
                        component(rightClickAction.description)
                        italic = false
                    }
                    +literalText("                    ") { color = KColors.DARKGRAY; strikethrough = true }
                }
                flag(ItemFlag.HIDE_ATTRIBUTES)
            }
        }
    }

    override fun perform(player: TeamPlayer) {
        val bukkitPlayer = player.player ?: return
        if (!playerHasItem(bukkitPlayer, item.type)) {
            player.sendMsg {
                text("You do not have any $entryName!") { color = KColors.RED }
            }
            bukkitPlayer.playSound(bukkitPlayer.location, Sound.ENTITY_VILLAGER_NO, 1f, 1f)
            return
        }

        val slot = bukkitPlayer.inventory.first(item.type)
        bukkitPlayer.inventory.getItem(slot)?.let { itemStack ->
            itemStack.amount -= 1
        }
        bukkitPlayer.sendMsg {
            text("You sold ") { color = KColors.GRAY }
            text("1x $entryName ") { color = KColors.AQUAMARINE }
            text("for ") { color = KColors.GRAY }
            text("$$price") { color = KColors.DARKAQUA }
            text(".") { color = KColors.GRAY }
        }
        player.balance += price
    }

    private fun playerHasItem(player: Player?, material: Material) = player?.inventory?.contains(material) ?: false
}