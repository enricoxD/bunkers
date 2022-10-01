package de.hglabor.hcfcore.mechanics.fixes

import com.destroystokyo.paper.MaterialTags
import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.configuration.TConfig
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.event.listen
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.Block
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.ItemStack

object PearlGlitchListener {
    private val refundPearls = TConfig.REFUND_PEARL_GLITCHING

    fun enable() {
        listen<ProjectileLaunchEvent>(ignoreCancelled = false) {
            if (it.entityType != EntityType.ENDER_PEARL) return@listen
            val player = it.entity.shooter as? Player ?: return@listen
            // TODO pearl timer
            taskRunLater(1) {
                player.setCooldown(Material.ENDER_PEARL, 15*20)
            }
        }

        listen<PlayerTeleportEvent>(ignoreCancelled = true, priority = EventPriority.NORMAL) {
            if (it.cause != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return@listen
            val to = it.to

            if (to.block.isPearlBlocked()) {
                val player = it.player
                player.sendMsg {
                    text("Pearl glitching detected! ") { color = KColors.RED; bold = true }
                    if (refundPearls) {
                        text("Your enderpearl has been refunded!") { color = KColors.RED }
                    }
                }

                if (refundPearls) {
                    // TODO remove pearl timer
                    player.setCooldown(Material.ENDER_PEARL, 0)
                    player.inventory.addItem(ItemStack(Material.ENDER_PEARL))
                }

                it.isCancelled = true
                return@listen
            }

            to.x = to.blockX + 0.5
            to.z = to.blockZ + 0.5
        }
    }

    private val materialsToCancel = setOf(
        Material.CARTOGRAPHY_TABLE
    )

    private fun Block.isPearlBlocked(): Boolean {
        if (Tag.STAIRS.isTagged(type)) return true
        if (Tag.FENCES.isTagged(type)) return true
        if (Tag.FENCE_GATES.isTagged(type)) return true
        if (MaterialTags.GLASS_PANES.isTagged(type)) return true
        return false
    }
}