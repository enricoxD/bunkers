package de.hglabor.bunkers.game.pvpstyle

import de.hglabor.bunkers.game.GameManager
import de.hglabor.common.extension.sendMsg
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.event.listen
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.axay.kspigot.items.setLore
import net.axay.kspigot.runnables.async
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

object PvPStyleVoteGUI {
    private val item = itemStack(Material.AMETHYST_CLUSTER) {
        meta {
            name = literalText("Vote PvPStyle") { color = KColors.AQUAMARINE }
        }
    }

    val listener = listen<PlayerInteractEvent> {
        if (it.hand == EquipmentSlot.OFF_HAND) return@listen
        if (!it.action.isRightClick) return@listen
        if (it.player.inventory.itemInMainHand != item) return@listen
        it.player.openGUI(guiBuilder())
    }

    fun giveItem(player: Player) {
        player.inventory.addItem(item)
    }

    private fun guiBuilder(): GUI<ForInventoryThreeByNine> {
        fun styleIcon(pvpStyle: IPvPStyle): ItemStack {
            return pvpStyle.displayItem.clone().apply {
                meta {
                    name = literalText(pvpStyle.name) { color = KColors.DARKAQUA; italic = false }

                    setLore {
                        +literalText {
                            text(" ● ") { color = KColors.DARKGRAY; italic = false }
                            text("Votes: ${GameManager.pvpStyleManager.getVotes(pvpStyle)}") { color = KColors.FLORALWHITE; italic = false }
                        }
                    }
                }
            }
        }

        return kSpigotGUI(GUIType.THREE_BY_NINE) {
            title = literalText("PvP Styles") { color = KColors.AQUAMARINE }

            page(1) {
                placeholder(Slots.All, itemStack(Material.WHITE_STAINED_GLASS_PANE) { meta { name = literalText("") } })
                val categoryGUICompound =
                    createRectCompound<IPvPStyle>(Slots.RowTwoSlotTwo, Slots.RowTwoSlotEight,
                        iconGenerator = { styleIcon(it) },
                        onClick = { clickEvent, element ->
                            val player = clickEvent.player
                            player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 1f)
                            player.closeInventory()
                            player.sendMsg {
                                text("You voted for ") { color = KColors.GRAY }
                                text("${element.name}") { color = KColors.AQUAMARINE }
                                text(".") { color = KColors.GRAY }
                            }
                            GameManager.pvpStyleManager.vote(clickEvent.player, element)
                        })
                async {
                    categoryGUICompound.setContent(GameManager.pvpStyleManager.allStyles)
                }
            }
        }
    }
}