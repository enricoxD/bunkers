package de.hglabor.bunkers.game.phase.phases

import de.hglabor.bunkers.game.GameManager
import de.hglabor.bunkers.game.phase.GamePhase
import de.hglabor.bunkers.listener.koth.BunkersCaptureKothEvent
import de.hglabor.bunkers.shop.ShopManager
import de.hglabor.bunkers.teams.BunkersTeam
import de.hglabor.common.extension.addOrDropItem
import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.event.koth.KothManager
import de.hglabor.hcfcore.manager.player.teamPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.extensions.bukkit.feedSaturate
import net.axay.kspigot.extensions.bukkit.heal
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object IngamePhase : GamePhase(1800, EndPhase) {

    override fun onStart() {
        onlinePlayers.forEach { player ->
            if (player.teamPlayer.team == null) {
                Core.teamManager.teams.filterIsInstance<BunkersTeam>()
                    .minBy { it.members.size }
                    .join(player.teamPlayer)
            }
            player.gameMode = GameMode.SURVIVAL
            player.inventory.clear()
            player.fireTicks = 0
            player.heal()
            player.feedSaturate()
            player.teamPlayer.team?.homeLocation?.let { location ->
                player.teleport(location)
            }
            player.inventory.addOrDropItem(ItemStack(Material.IRON_PICKAXE))
        }
        ShopManager.spawnAllVillagers()
        ShopManager.shops.forEach { shop ->
            shop.onInit()
        }
        KothManager.startRandomKoth()
        BunkersCaptureKothEvent.register()
        super.onStart()
    }

    override fun onTick() {
        super.onTick()

        Core.playerManager.teamPlayers.forEach { player ->
            (player.team as? BunkersTeam)?.let { team ->
                player.player?.sendActionBar(literalText {
                    text("Team ${team.name}") { color = team.teamColor }
                    text(" | ") { color = KColors.DIMGRAY }
                    text("DTR: ") { color = KColors.GRAY }
                    text("${team.dtr}") { color = KColors.WHITE }
                })
            }

            if (GameManager.elapsedTime != 0 && GameManager.elapsedTime % 3 == 0) {
                player.balance += 3
            }
        }
    }
}