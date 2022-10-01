package de.hglabor.bunkers.shop.entry

import de.hglabor.hcfcore.player.impl.TeamPlayer
import org.bukkit.inventory.ItemStack

interface IShopEntry {
    val entryName: String
    val item: ItemStack
    val price: Int
    val rightClickAction: RightClickAction?

    fun generateDisplayItem(player: TeamPlayer): ItemStack
    fun perform(player: TeamPlayer)

    fun performRightClick(player: TeamPlayer) {
        val action = rightClickAction
        if (action == null) perform(player)
        else action.onRightClick.invoke(player)
    }
}