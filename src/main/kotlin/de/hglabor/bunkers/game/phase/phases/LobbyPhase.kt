package de.hglabor.bunkers.game.phase.phases

import de.hglabor.auseinandersetzung.common.scoreboard.setScoreboard
import de.hglabor.bunkers.game.GameManager
import de.hglabor.bunkers.game.phase.GamePhase
import de.hglabor.bunkers.game.pvpstyle.PvPStyleVoteGUI
import de.hglabor.bunkers.teams.TeamSelector
import de.hglabor.common.extension.broadcast
import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.event.koth.KothManager
import de.hglabor.hcfcore.manager.player.teamPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.*

object LobbyPhase: GamePhase(120, IngamePhase) {

    override fun onTick() {
        if (onlinePlayers.size > 5) super.onTick()
        else GameManager.elapsedTime = 0

        when (remainingTime) {
            60, 30, 15, 10, 5, 3, 2, 1 -> broadcast {
                text("The game starts in ") { color = KColors.GRAY }
                text("$remainingTime") { color = KColors.DARKAQUA }
                text(".") { color = KColors.GRAY }
            }
        }
    }

    override fun onEnd() {
        PvPStyleVoteGUI.listener.unregister()
        TeamSelector.listener.unregister()
        GameManager.pvpStyleManager.selectFromPool()
        super.onEnd()
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        player.inventory.clear()
        PvPStyleVoteGUI.giveItem(player)
        TeamSelector.giveItem(player)

        player.setScoreboard {
            title = literalText("Bunkers")

            content {
                +{ literalText("Phase: ${GameManager.currentPhase::class.simpleName}") }
                +{ literalText("Time: ${GameManager.elapsedTime}") }
                +literalText("")
                +{ literalText("KOTH: ${KothManager.currentKoth?.timer?.remainingTime()}") }
                +literalText("")
                +{ literalText("Balance: $${player.teamPlayer.balance}") }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        event.player.teamPlayer.team?.members?.remove(event.player.uniqueId)
        Core.playerManager.cache.remove(event.player.uniqueId)
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onBlockBreak(event: BlockBreakEvent) {
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onBlockPlace(event: BlockPlaceEvent) {
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onEntityDamage(event: EntityDamageEvent) {
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onEntitySpawn(event: EntitySpawnEvent) {
        if (event.entity.entitySpawnReason == CreatureSpawnEvent.SpawnReason.CUSTOM) return
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onInteract(event: PlayerInteractEvent) {
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onDropItem(event: PlayerDropItemEvent) {
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onSwapHand(event: PlayerSwapHandItemsEvent) {
        event.isCancelled = true
    }
}