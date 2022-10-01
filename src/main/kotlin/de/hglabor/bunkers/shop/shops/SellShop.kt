package de.hglabor.bunkers.shop.shops

import de.hglabor.bunkers.shop.AbstractShop
import de.hglabor.bunkers.shop.ShopManager
import de.hglabor.bunkers.shop.entry.IShopEntry
import de.hglabor.bunkers.shop.entry.impl.SellEntry
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
class SellShop(
    override val teamName: String,
    @Serializable(with = LocationSerializer::class) override val location: Location
) : AbstractShop() {
    companion object {
        val entries: Map<InventorySlotCompound<*>, IShopEntry> = mapOf(
            Slots.RowThreeSlotFour to SellEntry("Coal", ItemStack(Material.COAL), 10),
            Slots.RowThreeSlotSix to SellEntry("Iron", ItemStack(Material.IRON_INGOT), 20),
            Slots.RowTwoSlotThree to SellEntry("Gold", ItemStack(Material.GOLD_INGOT), 25),
            Slots.RowTwoSlotSeven to SellEntry("Diamond", ItemStack(Material.DIAMOND), 30),
            Slots.RowOneSlotFive to SellEntry("Emerald", ItemStack(Material.EMERALD), 150)
        )
    }

    override val villagerProfession = Villager.Profession.MASON
    override val name: String = "Sell Shop"

    @Transient
    override val shopEntries: Map<InventorySlotCompound<*>, IShopEntry> = entries

    @Transient
    override var villager: Entity? = null

    override fun openGUI(player: TeamPlayer) {
        player.player?.openGUI(kSpigotGUI(GUIType.THREE_BY_NINE) {
            title = literalText(this@SellShop.name) { color = KColors.AQUAMARINE }

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