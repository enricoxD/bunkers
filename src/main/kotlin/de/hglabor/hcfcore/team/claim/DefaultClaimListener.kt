package de.hglabor.hcfcore.team.claim

import com.destroystokyo.paper.MaterialSetTag
import com.destroystokyo.paper.MaterialTags
import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.manager.player.teamPlayer
import de.hglabor.hcfcore.team.claim.impl.SpawnClaim
import de.hglabor.hcfcore.team.impl.PlayerTeam
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.event.listen
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.Block
import org.bukkit.block.Lockable
import org.bukkit.block.TileState
import org.bukkit.block.data.Openable
import org.bukkit.block.data.Powerable
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.*

object DefaultClaimListener {
    fun enable() {
        listen<BlockPlaceEvent> {
            if (it.isCancelled) return@listen
            cancelEvent(it, it.player, it.block.location)
        }

        listen<BlockBreakEvent> {
            if (it.isCancelled) return@listen
            cancelEvent(it, it.player, it.block.location)
        }

        listen<PlayerInteractEvent> {
            if (it.useItemInHand() == Event.Result.DENY && it.useInteractedBlock() == Event.Result.DENY) return@listen
            cancelEvent(it, it.player, it.clickedBlock?.location ?: return@listen)
        }

        listen<PlayerBucketEmptyEvent> {
            if (it.isCancelled) return@listen
            cancelEvent(it, it.player, it.block.location)
        }

        listen<PlayerBucketFillEvent> {
            if (it.isCancelled) return@listen
            cancelEvent(it, it.player, it.block.location)
        }

        listen<PlayerBucketEntityEvent> {
            if (it.isCancelled) return@listen
            cancelEvent(it, it.player, it.entity.location)
        }

        listen<EntityDamageByEntityEvent> {
            val damager = it.damager as? Player ?: return@listen
            val entity = it.entity as? Player ?: return@listen

            if (damager.teamPlayer.claim is SpawnClaim || entity.teamPlayer.claim is SpawnClaim) {
                it.isCancelled = true
            }
        }
    }

    private fun cancelEvent(event: Cancellable, player: Player, location: Location) {
        val claim = Core.claimManager.getClaimAt(location) ?: return

        if (event is BlockEvent || event is PlayerBucketEvent) {
            if (shouldCancelBlockEvent(player, claim)) {
                event.isCancelled = true
                player.sendMsg {
                    text("You cannot build in ") { color = KColors.GRAY }
                    text("${claim.name}'s ") { color = KColors.AQUAMARINE }
                    text("territory.") { color = KColors.GRAY }
                }
            }
            return
        }

        if (event is PlayerInteractEvent) {
            if (shouldCancelInteractEvent(player, claim, event)) {
                event.isCancelled = true
                player.sendMsg {
                    text("You cannot do this in ") { color = KColors.GRAY }
                    text("${claim.name}'s ") { color = KColors.AQUAMARINE }
                    text("territory.") { color = KColors.GRAY }
                }
                event.setUseItemInHand(Event.Result.ALLOW)
            }
        }
    }

    private fun shouldCancelBlockEvent(player: Player, claim: IClaim): Boolean {
        val teamPlayer = player.teamPlayer
        if (claim.name == teamPlayer.teamName) return false
        val team = Core.teamManager.teamByName(claim.name) ?: return false
        if (team !is PlayerTeam) return true
        return !team.isRaidable
    }

    private fun shouldCancelInteractEvent(player: Player, claim: IClaim, event: PlayerInteractEvent): Boolean {
        if (event.action.isLeftClick) return false
        val teamPlayer = player.teamPlayer
        if (claim.name == teamPlayer.teamName) return false
        val team = Core.teamManager.teamByName(claim.name) ?: return false

        if (event.action == Action.PHYSICAL) {
            val type = event.clickedBlock?.type
            if (type != null && Tag.PRESSURE_PLATES.isTagged(type)) {
                event.isCancelled = true
                return false
            }
            return true
        }

        // Cancel things like unwaxing copper, stripping logs, hoeing dirt etc
        val itemInMainHand = player.inventory.itemInMainHand
        val clickedBlock = event.clickedBlock
        if (clickedBlock != null) {
            when {
                MaterialTags.AXES.isTagged(itemInMainHand) || itemInMainHand.type == Material.HONEYCOMB -> {
                    when {
                        MaterialTags.COPPER_BLOCKS.isTagged(clickedBlock.type) || MaterialSetTag.LOGS.isTagged(clickedBlock.type) -> {
                            return true
                        }
                    }
                }

                MaterialTags.HOES.isTagged(itemInMainHand) -> {
                    if (clickedBlock.type.name.contains("dirt", true)) {
                        return true
                    }
                }
            }

            when (itemInMainHand.type) {
                Material.SHEARS -> if (clickedBlock.type == Material.PUMPKIN) return true
                Material.GLOWSTONE -> if (clickedBlock.type == Material.RESPAWN_ANCHOR) return true
                Material.FLINT_AND_STEEL -> if (clickedBlock.type == Material.TNT) return true
                else -> {}
            }
        }

        if (team is PlayerTeam) {
            if (team.isRaidable) return false
        }

        if (event.clickedBlock.isInteractable()) return true
        return false
    }

    private val materialsToCancel = setOf(
        Material.CARTOGRAPHY_TABLE,
        Material.GRINDSTONE,
        Material.SMITHING_TABLE,
        Material.STONECUTTER,
        Material.COMPOSTER,
        Material.LOOM,
        Material.CRAFTING_TABLE
    )

    private fun Block?.isInteractable(): Boolean {
        if (this == null) return false
        val material = type

        if (this is Openable) return true
        if (this is Lockable) return true
        if (this.blockData is Powerable) return true

        if (this.state is TileState) {
            return when {
                Tag.BANNERS.isTagged(material) -> false
                MaterialTags.SKULLS.isTagged(material) -> false
                material == Material.END_GATEWAY -> false
                else -> true
            }
        }

        if (Tag.ANVIL.isTagged(type)) return true
        if (Tag.BEEHIVES.isTagged(type)) return true
        if (Tag.BEE_GROWABLES.isTagged(type)) return true
        if (Tag.CAULDRONS.isTagged(type)) return true
        if (Tag.FLOWER_POTS.isTagged(type)) return true
        if (materialsToCancel.contains(type)) return true
        return false
    }
}