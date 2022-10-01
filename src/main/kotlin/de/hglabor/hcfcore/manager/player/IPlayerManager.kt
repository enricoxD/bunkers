package de.hglabor.hcfcore.manager.player

import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.manager.IManager
import de.hglabor.hcfcore.player.impl.TeamPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.bukkit.kill
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

interface IPlayerManager: IManager<UUID, TeamPlayer> {
    override fun enable() {
        listen<PlayerJoinEvent> {
            val player = it.player
            val teamPlayer = cache.computeIfAbsent(player.uniqueId) {
                TeamPlayer(player.uniqueId, player.name)
            }
            teamPlayer.name = player.name
            teamPlayer.team?.notify {
                text("Member online: ") { color = KColors.LIME }
                text(player.name) { color = KColors.WHITE }
            }
        }

        listen<PlayerQuitEvent> {
            val player = it.player
            val teamPlayer = cache.computeIfAbsent(player.uniqueId) {
                TeamPlayer(player.uniqueId, player.name)
            }
            teamPlayer.team?.notify {
                text("Member offline: ") { color = KColors.RED }
                text(player.name) { color = KColors.WHITE }
            }
        }

        listen<PlayerDeathEvent>(priority = EventPriority.HIGHEST, ignoreCancelled = false) {
            if (it.isCancelled) return@listen
            it.player.teamPlayer.statistics.deaths += 1

            it.player.killer?.let { killer ->
                killer.teamPlayer.statistics.kills += 1
            }
        }
    }

    val teamPlayers: List<TeamPlayer> get() = cache.values.toList()
}

fun teamPlayerByName(name: String) = Bukkit.getPlayer(name)?.teamPlayer
fun teamPlayerByUUID(uuid: UUID) = Core.playerManager.cache[uuid]
val Player.teamPlayer get() = Core.playerManager.cache.computeIfAbsent(uniqueId) { TeamPlayer(uniqueId, name) }
