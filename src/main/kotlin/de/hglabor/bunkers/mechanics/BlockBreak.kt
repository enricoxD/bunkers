package de.hglabor.bunkers.mechanics

import de.hglabor.bunkers.teams.BunkersTeam
import de.hglabor.common.extension.addOrDropItem
import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.manager.player.teamPlayer
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent

object BlockBreak {
    private val ores = setOf(
        Material.COAL_ORE,
        Material.IRON_ORE,
        Material.GOLD_ORE,
        Material.DIAMOND_ORE,
        Material.EMERALD_ORE
    )
    private val replacements = mapOf(
        Material.RAW_IRON to Material.IRON_INGOT,
        Material.RAW_GOLD to Material.GOLD_INGOT
    )
    private val regenerating = mutableSetOf<Location>()

    fun enable() {
        // Add block to inventory
        listen<BlockBreakEvent>(priority = EventPriority.HIGHEST, ignoreCancelled = false) {
            if (it.isCancelled) return@listen
            val player = it.player
            it.isDropItems = false
            it.expToDrop = 0

            it.block.drops.forEach { drop ->
                drop.type = replacements[drop.type] ?: drop.type
                player.inventory.addOrDropItem(drop)
            }
        }

        // Regenearte ores and replace its drops
        listen<BlockBreakEvent>(priority = EventPriority.HIGH, ignoreCancelled = false) {
            val type = it.block.type
            if (type !in ores) return@listen

            val team = Core.claimManager.getTeamAt(it.block.location)
            if (team is BunkersTeam && team != it.player.teamPlayer.team && !team.isRaidable) {
                it.isCancelled = true
                return@listen
            }

            taskRunLater(1) {
                regenerating += it.block.location
                val regeneratingMaterial = Material.getMaterial("DEEPSLATE_${type.name}") ?: Material.DEEPSLATE
                it.block.setType(regeneratingMaterial, false)
                taskRunLater(100) {
                    regenerating -= it.block.location
                    it.block.setType(type, false)
                }
            }
        }

        // Stop breaking regenerating blocks
        listen<BlockBreakEvent>(priority = EventPriority.HIGH, ignoreCancelled = false) {
            if (it.block.location in regenerating) it.isCancelled = true
        }

        // Stop breaking blocks in unclaimed regions
        listen<BlockBreakEvent>(priority = EventPriority.HIGH, ignoreCancelled = false) {
            if (Core.claimManager.getClaimAt(it.block.location) == null) {
                if (ores.contains(it.block.type)) return@listen
                it.isCancelled = true
            }
        }
    }
}