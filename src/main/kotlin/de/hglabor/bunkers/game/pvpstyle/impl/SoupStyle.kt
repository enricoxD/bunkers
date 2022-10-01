package de.hglabor.bunkers.game.pvpstyle.impl

import de.hglabor.bunkers.game.pvpstyle.IPvPStyle
import de.hglabor.bunkers.shop.entry.IShopEntry
import de.hglabor.bunkers.shop.entry.impl.*
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.InventorySlotCompound
import net.axay.kspigot.gui.Slots
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionType
import kotlin.math.min

object SoupStyle: IPvPStyle {
    override val name: String = "Soup"
    override val displayItem: ItemStack = ItemStack(Material.MUSHROOM_STEW)

    override fun enable() {
        onlinePlayers.forEach {
            it.getAttribute(Attribute.GENERIC_ATTACK_SPEED)?.baseValue = 100.0
        }
        listen<PlayerJoinEvent> {
            it.player.getAttribute(Attribute.GENERIC_ATTACK_SPEED)?.baseValue = 100.0
        }

        listen<PlayerRespawnEvent> {
            it.player.getAttribute(Attribute.GENERIC_ATTACK_SPEED)?.baseValue = 100.0
        }

        listen<PlayerInteractEvent> {
            if (it.action == Action.LEFT_CLICK_AIR) return@listen
            it.player.apply {
                if (inventory.itemInMainHand.type != Material.MUSHROOM_STEW) return@listen
                if (health >= maxHealth - 0.4) return@listen
                health = min(maxHealth, health + 7)
                itemInHand.type = Material.BOWL
                updateInventory()
            }
        }
    }

    override val shopEntries: Map<InventorySlotCompound<*>, IShopEntry> = mapOf(
        // Sword
        Slots.RowFourSlotOne to BuyEntry("Diamond Sword", ItemStack(Material.DIAMOND_SWORD), 100),
        // Armor
        Slots.RowFiveSlotTwo to ArmorEntry("Iron Helmet", EquipmentSlot.HEAD, ItemStack(Material.IRON_HELMET), 75),
        Slots.RowFourSlotTwo to ArmorEntry("Iron Chestplate", EquipmentSlot.CHEST, ItemStack(Material.IRON_CHESTPLATE), 200),
        Slots.RowThreeSlotTwo to ArmorEntry("Iron Leggings", EquipmentSlot.LEGS, ItemStack(Material.IRON_LEGGINGS), 150),
        Slots.RowTwoSlotTwo to ArmorEntry("Iron Boots", EquipmentSlot.FEET, ItemStack(Material.IRON_BOOTS), 75),
        // Full Set
        Slots.RowFourSlotThree to SetEntry("Iron Set", ItemStack(Material.IRON_INGOT), 600,
            mapOf(
                EquipmentSlot.HEAD to ItemStack(Material.IRON_HELMET),
                EquipmentSlot.CHEST to ItemStack(Material.IRON_CHESTPLATE),
                EquipmentSlot.LEGS to ItemStack(Material.IRON_LEGGINGS),
                EquipmentSlot.FEET to ItemStack(Material.IRON_BOOTS),
                EquipmentSlot.HAND to ItemStack(Material.DIAMOND_SWORD)
            )
        ),
        // Pearl
        Slots.RowFourSlotFour to BuyMultipleEntry("Enderpearl", ItemStack(Material.ENDER_PEARL), 25),

        // Misc
        Slots.RowTwoSlotFive to BuyEntry("Potatoes", ItemStack(Material.BAKED_POTATO, 16), 5),

        // Soup and Buff pots
        Slots.RowFiveSlotSix to BuyMultipleEntry("Mushroom Stew", ItemStack(Material.MUSHROOM_STEW), 10),
        Slots.RowFiveSlotSeven to PotionEntry("Fire Resistance Potion (8:00)", PotionData(PotionType.FIRE_RESISTANCE, true, false), false, 25),
        Slots.RowFiveSlotEight to PotionEntry("Invisibility Potion (3:00)", PotionData(PotionType.INVISIBILITY), false, 1250),

        // Recraft
        Slots.RowFourSlotSix to BuyEntry("Brown Mushrooms", ItemStack(Material.BROWN_MUSHROOM, 16), 240),
        Slots.RowFourSlotSeven to BuyEntry("Bowls", ItemStack(Material.BOWL, 16), 240),
        Slots.RowFourSlotEight to BuyEntry("Red Mushrooms", ItemStack(Material.RED_MUSHROOM, 16), 240),
    )
}