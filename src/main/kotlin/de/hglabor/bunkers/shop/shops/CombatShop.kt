package de.hglabor.bunkers.shop.shops

import de.hglabor.bunkers.game.GameManager
import de.hglabor.bunkers.shop.AbstractShop
import de.hglabor.bunkers.shop.entry.IShopEntry
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

@Serializable
open class CombatShop(
    override val teamName: String,
    @Serializable(with = LocationSerializer::class) override val location: Location
): AbstractShop() {

    override val villagerProfession = Villager.Profession.ARMORER
    override val name: String = "Combat Shop"
    override val shopEntries: Map<InventorySlotCompound<*>, IShopEntry> by lazy { GameManager.pvpStyleManager.style?.shopEntries ?: mapOf() }
    @Transient
    override var villager: Entity? = null

    override fun openGUI(player: TeamPlayer) {
        player.player?.openGUI(kSpigotGUI(GUIType.SIX_BY_NINE) {
            title = literalText(this@CombatShop.name) { color = KColors.AQUAMARINE }

            page(1) {
                placeholder(Slots.All, itemStack(Material.WHITE_STAINED_GLASS_PANE) { meta { name = literalText("") } })

                shopEntries.forEach { (slot, entry) ->
                    val slot = slot as InventorySlotCompound<ForInventorySixByNine>
                    button(slot, entry.generateDisplayItem(player)) {
                        if (it.bukkitEvent.isRightClick) entry.performRightClick(player)
                        else entry.perform(player)
                    }
                }
            }
        })
    }
}