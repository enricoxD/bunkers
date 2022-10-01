package de.hglabor.bunkers.game.pvpstyle.impl

import de.hglabor.bunkers.game.pvpstyle.IPvPStyle
import de.hglabor.bunkers.shop.entry.IShopEntry
import de.hglabor.bunkers.shop.entry.impl.*
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.gui.InventorySlotCompound
import net.axay.kspigot.gui.Slots
import net.axay.kspigot.items.flag
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionType

object PotStyle: IPvPStyle {
    override val name: String = "PotPvP"
    override val displayItem: ItemStack = itemStack(Material.SPLASH_POTION) {
        meta<PotionMeta> {
            basePotionData = PotionData(PotionType.INSTANT_HEAL)
            flag(ItemFlag.HIDE_ATTRIBUTES)
        }
    }

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
    }

    override val shopEntries: Map<InventorySlotCompound<*>, IShopEntry> = mapOf(
        // Sword
        Slots.RowFourSlotOne to BuyEntry("Diamond Sword", ItemStack(Material.DIAMOND_SWORD), 100),
        // Armor
        Slots.RowFiveSlotTwo to ArmorEntry("Diamond Helmet", EquipmentSlot.HEAD, ItemStack(Material.DIAMOND_HELMET), 75),
        Slots.RowFourSlotTwo to ArmorEntry("Diamond Chestplate", EquipmentSlot.CHEST, ItemStack(Material.DIAMOND_CHESTPLATE), 200),
        Slots.RowThreeSlotTwo to ArmorEntry("Diamond Leggings", EquipmentSlot.LEGS, ItemStack(Material.DIAMOND_LEGGINGS), 150),
        Slots.RowTwoSlotTwo to ArmorEntry("Diamond Boots", EquipmentSlot.FEET, ItemStack(Material.DIAMOND_BOOTS), 75),
        // Full Set
        Slots.RowFourSlotThree to SetEntry("Diamond Set", ItemStack(Material.DIAMOND), 600,
            mapOf(
                EquipmentSlot.HEAD to ItemStack(Material.DIAMOND_HELMET),
                EquipmentSlot.CHEST to ItemStack(Material.DIAMOND_CHESTPLATE),
                EquipmentSlot.LEGS to ItemStack(Material.DIAMOND_LEGGINGS),
                EquipmentSlot.FEET to ItemStack(Material.DIAMOND_BOOTS),
                EquipmentSlot.HAND to ItemStack(Material.DIAMOND_SWORD)
            )
        ),
        // Pearl
        Slots.RowFourSlotFour to BuyMultipleEntry("Enderpearl", ItemStack(Material.ENDER_PEARL), 25),

        // Misc
        Slots.RowTwoSlotFive to BuyEntry("Potatoes", ItemStack(Material.BAKED_POTATO, 16), 5),

        // Pots
        Slots.RowFiveSlotSix to PotionEntry("Speed II Potion (1:30)", PotionData(PotionType.SPEED, false, true), false, 10),
        Slots.RowFiveSlotSeven to PotionEntry("Fire Resistance Potion (8:00)", PotionData(PotionType.FIRE_RESISTANCE, true, false), false, 25),
        Slots.RowFiveSlotEight to PotionEntry("Invisibility Potion (3:00)", PotionData(PotionType.INVISIBILITY), false, 1250),

        Slots.RowFourSlotSix to BuyMultipleEntry(
            "Instant Health II Splash Potion", itemStack(Material.SPLASH_POTION) {
                meta<PotionMeta> {
                    basePotionData = PotionData(PotionType.INSTANT_HEAL, false, true)
                }
            }, 10),
        Slots.RowFourSlotSeven to PotionEntry("Poison Splash Potion (0:33)", PotionData(PotionType.POISON), true, 50) ,
        Slots.RowFourSlotEight to PotionEntry("Slowness Splash Potion (1:30)", PotionData(PotionType.SLOWNESS), true, 50)
    )
}