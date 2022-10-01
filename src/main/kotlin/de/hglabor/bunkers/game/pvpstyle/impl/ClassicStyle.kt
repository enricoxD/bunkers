package de.hglabor.bunkers.game.pvpstyle.impl

import de.hglabor.bunkers.game.pvpstyle.IPvPStyle
import de.hglabor.bunkers.shop.entry.IShopEntry
import de.hglabor.bunkers.shop.entry.impl.ArmorEntry
import de.hglabor.bunkers.shop.entry.impl.BuyEntry
import de.hglabor.bunkers.shop.entry.impl.BuyMultipleEntry
import de.hglabor.bunkers.shop.entry.impl.SetEntry
import net.axay.kspigot.event.listen
import net.axay.kspigot.gui.InventorySlotCompound
import net.axay.kspigot.gui.Slots
import net.axay.kspigot.runnables.KSpigotRunnable
import net.axay.kspigot.runnables.task
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerBucketFillEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

object ClassicStyle: IPvPStyle {
    override val name: String = "Classic 1.19"
    override val displayItem: ItemStack = ItemStack(Material.IRON_AXE)

    override fun enable() {
        val bucketTask = mutableMapOf<Block, KSpigotRunnable?>()

        listen<PlayerBucketEmptyEvent>(priority = EventPriority.HIGHEST) {
            if (it.isCancelled) return@listen
            it.player.inventory.setItemInMainHand(ItemStack(Material.AIR))
            val block = it.block
            bucketTask[block]?.cancel()
            bucketTask[block] = task(true, 200, 0, 1) {
                block.world.playSound(block.location, Sound.ITEM_BUCKET_FILL, 1f, 1f)
                block.setType(Material.AIR, true)
            }
        }

        listen<PlayerBucketFillEvent>(priority = EventPriority.HIGHEST) {
            if (it.isCancelled) return@listen
            it.player.inventory.setItemInMainHand(ItemStack(Material.AIR))
            val block = it.block
            bucketTask[block]?.cancel()
            bucketTask.remove(block)
        }

        listen<BlockPlaceEvent>(priority = EventPriority.HIGHEST) {
            if (it.isCancelled) return@listen
            val block = it.blockPlaced
            if (block.type != Material.COBWEB) return@listen
            bucketTask[block]?.cancel()
            bucketTask[block] = task(true, 200, 0, 1) {
                block.world.playSound(block.location, Sound.BLOCK_STONE_BREAK, 1f, 1f)
                block.setType(Material.AIR, true)
            }
        }

        listen<BlockBreakEvent>(priority = EventPriority.HIGHEST) {
            if (it.isCancelled) return@listen
            val block = it.block
            if (block.type != Material.COBWEB) return@listen
            bucketTask[block]?.cancel()
            bucketTask.remove(block)
        }

        listen<BlockFromToEvent> {
            it.isCancelled = true
        }
    }

    override val shopEntries: Map<InventorySlotCompound<*>, IShopEntry> = mapOf(
        // Sword
        Slots.RowFourSlotOne to BuyEntry("Diamond Sword", ItemStack(Material.DIAMOND_SWORD), 100),
        Slots.RowThreeSlotOne to BuyEntry("Diamond Axe", ItemStack(Material.DIAMOND_AXE), 125),
        // Armor
        Slots.RowFiveSlotTwo to ArmorEntry("Diamond Helmet", EquipmentSlot.HEAD, ItemStack(Material.DIAMOND_HELMET), 75),
        Slots.RowFourSlotTwo to ArmorEntry("Diamond Chestplate", EquipmentSlot.CHEST, ItemStack(Material.DIAMOND_CHESTPLATE), 200),
        Slots.RowThreeSlotTwo to ArmorEntry("Diamond Leggings", EquipmentSlot.LEGS, ItemStack(Material.DIAMOND_LEGGINGS), 150),
        Slots.RowTwoSlotTwo to ArmorEntry("Diamond Boots", EquipmentSlot.FEET, ItemStack(Material.DIAMOND_BOOTS), 75),
        // Full Set
        Slots.RowFourSlotThree to SetEntry("Diamond Set", ItemStack(Material.DIAMOND), 725,
            mapOf(
                EquipmentSlot.HEAD to ItemStack(Material.DIAMOND_HELMET),
                EquipmentSlot.CHEST to ItemStack(Material.DIAMOND_CHESTPLATE),
                EquipmentSlot.LEGS to ItemStack(Material.DIAMOND_LEGGINGS),
                EquipmentSlot.FEET to ItemStack(Material.DIAMOND_BOOTS),
                EquipmentSlot.HAND to ItemStack(Material.DIAMOND_SWORD),
                EquipmentSlot.OFF_HAND to ItemStack(Material.DIAMOND_AXE),
            )
        ),
        // Shield
        Slots.RowFourSlotFour to BuyEntry("Shield", ItemStack(Material.SHIELD), 150),

        // Misc
        Slots.RowTwoSlotFive to BuyEntry("Potatoes", ItemStack(Material.BAKED_POTATO, 16), 5),

        // Crapples, Buckets
        Slots.RowFiveSlotSix to BuyEntry("Golden Apples", ItemStack(Material.GOLDEN_APPLE, 4), 25),
        Slots.RowFiveSlotSeven to BuyEntry("Water Bucket", ItemStack(Material.WATER_BUCKET), 100),
        Slots.RowFiveSlotEight to BuyEntry("Lava Bucket", ItemStack(Material.LAVA_BUCKET), 250),

        // TODO
        //Slots.RowFourSlotSix to BuyEntry("Brown Mushrooms", ItemStack(Material.BROWN_MUSHROOM, 16), 240),
        Slots.RowFourSlotSeven to BuyEntry("Cobwebs", ItemStack(Material.COBWEB, 2), 150),
        //Slots.RowFourSlotEight to BuyEntry("Red Mushrooms", ItemStack(Material.RED_MUSHROOM, 16), 240),
    )
}