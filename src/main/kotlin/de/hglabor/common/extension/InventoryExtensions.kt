package de.hglabor.common.extension

import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

fun PlayerInventory.addOrDropItem(itemStack: ItemStack) {
    addItem(itemStack).onEach { (_, item) ->
        val player = holder ?: return
        player.world.dropItem(player.location, item)
    }
}

fun PlayerInventory.setOrAdd(equipmentSlot: EquipmentSlot, itemStack: ItemStack) {
    if (getItem(equipmentSlot).type == Material.AIR) setItem(equipmentSlot, itemStack)
    else addOrDropItem(itemStack)
}