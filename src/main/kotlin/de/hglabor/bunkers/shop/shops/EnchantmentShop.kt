package de.hglabor.bunkers.shop.shops

import com.destroystokyo.paper.MaterialTags
import de.hglabor.bunkers.shop.AbstractShop
import de.hglabor.bunkers.shop.entry.IShopEntry
import de.hglabor.bunkers.shop.entry.impl.EnchantmentEntry
import de.hglabor.common.extension.sendMsg
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
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
import org.bukkit.entity.Villager

@Serializable
class EnchantmentShop(
    override val teamName: String,
    @Serializable(with = LocationSerializer::class) override val location: Location
) : AbstractShop() {
    companion object {
        val entries: Map<InventorySlotCompound<*>, IShopEntry> = mapOf(
            // Tools
            Slots.RowTwoSlotTwo to EnchantmentEntry(
                "Sharpness I",
                Enchantment.DAMAGE_ALL,
                1,
                description = literalText { text("Enchant one Sword and one Axe in your inventory") },
                400
            ) { player, entry ->
                val sword = player.inventory.firstOrNull { it != null && MaterialTags.SWORDS.isTagged(it) && !it.containsEnchantment(entry.enchantment) }
                val axe = player.inventory.firstOrNull { it != null && MaterialTags.AXES.isTagged(it) && !it.containsEnchantment(entry.enchantment) }

                if (axe == null && sword == null) {
                    player.sendMsg {
                        text("You have neither an axe nor a sword to enchant.") { color = KColors.RED }
                    }
                    return@EnchantmentEntry false
                }

                axe?.addEnchantment(entry.enchantment, entry.enchantmentLevel)
                sword?.addEnchantment(entry.enchantment, entry.enchantmentLevel)
                return@EnchantmentEntry true
            },

            Slots.RowTwoSlotThree to EnchantmentEntry(
                "Full Protection I",
                Enchantment.PROTECTION_ENVIRONMENTAL,
                1,
                description = literalText { text("Enchant your entire armor") },
                1200
            ) { player, entry ->
                val armor = player.inventory.armorContents.filterNotNull()
                if (armor.size != 4) {
                    player.sendMsg {
                        text("You don't have a full armor.") { color = KColors.RED }
                    }
                    return@EnchantmentEntry false
                }
                val unenchantedArmor = armor.filter { !it.containsEnchantment(entry.enchantment) }
                if (unenchantedArmor.isEmpty()) {
                    player.sendMsg {
                        text("You don't have any armor to be enchanted.") { color = KColors.RED }
                    }
                    return@EnchantmentEntry false
                }
                unenchantedArmor.forEach { item ->
                    item.addEnchantment(entry.enchantment, entry.enchantmentLevel)
                }
                return@EnchantmentEntry true
            },

            Slots.RowTwoSlotFour to EnchantmentEntry(
                "Feather Falling IV",
                Enchantment.PROTECTION_FALL,
                4,
                description = literalText { text("Enchant your boots") },
                200
            ) { player, entry ->
                val boots = player.inventory.boots

                if (boots == null || !boots.type.name.endsWith("boots", true) || boots.containsEnchantment(Enchantment.PROTECTION_FALL)) {
                    player.sendMsg {
                        text("You don't have boots to enchant.") { color = KColors.RED }
                    }
                    return@EnchantmentEntry false
                }

                boots.addEnchantment(entry.enchantment, entry.enchantmentLevel)
                return@EnchantmentEntry true
            }
        )
    }

    override val villagerProfession = Villager.Profession.LIBRARIAN
    override val name: String = "Enchantment Shop"

    @Transient
    override val shopEntries: Map<InventorySlotCompound<*>, IShopEntry> = entries

    @Transient
    override var villager: Entity? = null

    override fun openGUI(player: TeamPlayer) {
        player.player?.openGUI(kSpigotGUI(GUIType.THREE_BY_NINE) {
            title = literalText(this@EnchantmentShop.name) { color = KColors.AQUAMARINE }

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