package de.hglabor.bunkers.game.pvpstyle

import de.hglabor.bunkers.shop.entry.IShopEntry
import net.axay.kspigot.gui.InventorySlotCompound
import org.bukkit.inventory.ItemStack

interface IPvPStyle {
    val name: String
    val displayItem: ItemStack
    val shopEntries: Map<InventorySlotCompound<*>, IShopEntry>

    fun enable()
}