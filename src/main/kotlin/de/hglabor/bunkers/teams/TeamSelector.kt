package de.hglabor.bunkers.teams

import de.hglabor.hcfcore.manager.player.teamPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.event.listen
import net.axay.kspigot.gui.*
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.items.meta
import net.axay.kspigot.items.name
import net.axay.kspigot.items.setLore
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

object TeamSelector {
    private val item = itemStack(Material.CYAN_BED) {
        meta {
            name = literalText("Teams") { color = KColors.AQUAMARINE }
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
        fun teamIcon(team: BunkersTeam): ItemStack {
            return itemStack(team.iconMaterial) {
                meta {
                    name = literalText(team.name) { color = team.teamColor; italic = false }

                    setLore {
                        team.players.forEach { player ->
                            +literalText {
                                text(" ● ") { color = KColors.DARKGRAY; italic = false }
                                text(player.name) { color = KColors.FLORALWHITE; italic = false }
                            }
                        }
                    }
                }
            }
        }

        return kSpigotGUI(GUIType.THREE_BY_NINE) {
            title = literalText("Teams") { color = KColors.AQUAMARINE }

            page(1) {
                placeholder(Slots.All, itemStack(Material.WHITE_STAINED_GLASS_PANE) { meta { name = literalText("") } })
                button(Slots.RowTwoSlotTwo, teamIcon(TeamManager.blue)) {
                    TeamManager.blue.join(it.player.teamPlayer)
                }

                button(Slots.RowTwoSlotFour, teamIcon(TeamManager.green)) {
                    TeamManager.green.join(it.player.teamPlayer)
                }

                button(Slots.RowTwoSlotSix, teamIcon(TeamManager.yellow)) {
                    TeamManager.yellow.join(it.player.teamPlayer)
                }

                button(Slots.RowTwoSlotEight, teamIcon(TeamManager.red)) {
                    TeamManager.red.join(it.player.teamPlayer)
                }
            }
        }
    }
}