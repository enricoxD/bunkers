package de.hglabor.bunkers.shop.shops

import de.hglabor.bunkers.shop.AbstractShop
import de.hglabor.bunkers.shop.ShopManager
import de.hglabor.bunkers.shop.entry.IShopEntry
import de.hglabor.bunkers.shop.entry.impl.BuyEntry
import de.hglabor.hcfcore.player.impl.TeamPlayer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.axay.kspigot.serialization.LocationSerializer
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.Villager
import org.bukkit.inventory.ItemStack

@Serializable
class BuildShop(
    override val teamName: String,
    @Serializable(with = LocationSerializer::class) override val location: Location
): AbstractShop() {
    companion object {
        val entries: Map<InventorySlotCompound<*>, IShopEntry> = mapOf(
            // Tools
            Slots.RowOneSlotFour to BuyEntry("Diamond Pickaxe", ItemStack(Material.DIAMOND_PICKAXE), 50),
            Slots.RowOneSlotFive to BuyEntry("Diamond Axe", ItemStack(Material.DIAMOND_AXE), 50),
            Slots.RowOneSlotSix to BuyEntry("Diamond Shovel", ItemStack(Material.DIAMOND_SHOVEL), 50),

            // Blocks
            Slots.RowThreeSlotTwo to BuyEntry("Chests", ItemStack(Material.CHEST, 16), 75),
            Slots.RowThreeSlotThree to BuyEntry("Stone", ItemStack(Material.STONE, 16), 75),
            Slots.RowThreeSlotFour to BuyEntry("Cobblestone", ItemStack(Material.COBBLESTONE, 16), 75),
            Slots.RowThreeSlotFive to BuyEntry("Fence Gate", ItemStack(Material.OAK_FENCE_GATE, 16), 75),
            Slots.RowThreeSlotSix to BuyEntry("Pressure Plate", ItemStack(Material.STONE_PRESSURE_PLATE, 16), 75),
            Slots.RowThreeSlotSeven to BuyEntry("Ladder", ItemStack(Material.LADDER, 16), 75),
            Slots.RowThreeSlotEight to BuyEntry("Button", ItemStack(Material.STONE_BUTTON, 16), 75)
        )
    }

    override val villagerProfession = Villager.Profession.TOOLSMITH
    override val name: String = "Build Shop"
    @Transient
    override val shopEntries: Map<InventorySlotCompound<*>, IShopEntry> = entries
    @Transient
    override var villager: Entity? = null

    override fun openGUI(player: TeamPlayer) {
        player.player?.openGUI(kSpigotGUI(GUIType.THREE_BY_NINE) {
            title = literalText(this@BuildShop.name) { color = KColors.AQUAMARINE }

            page(1) {
                placeholder(Slots.All, itemStack(Material.WHITE_STAINED_GLASS_PANE) { meta { name = literalText("") } })

                shopEntries.forEach { (slot, entry) ->
                    val slot = slot as InventorySlotCompound<ForInventoryThreeByNine>
                    button(slot, entry.generateDisplayItem(player)) {
                        if (it.bukkitEvent.isRightClick) entry.performRightClick(player)
                        else entry.perform(player)
                    }
                }
            }
        })
    }
}