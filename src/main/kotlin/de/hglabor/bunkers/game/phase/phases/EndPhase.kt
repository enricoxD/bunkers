package de.hglabor.bunkers.game.phase.phases

import de.hglabor.bunkers.game.phase.GamePhase
import de.hglabor.bunkers.teams.BunkersTeam
import de.hglabor.common.extension.broadcast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.event.SingleListener
import org.bukkit.Bukkit
import org.bukkit.event.Event
import kotlin.coroutines.coroutineContext

object EndPhase: GamePhase(20, null) {
    var winner: BunkersTeam? = null

    override fun onStart() {
        CoroutineScope(Dispatchers.IO).launch {
            repeat(5) {
                broadcast {
                    text("${winner?.name} ") { color = winner?.teamColor ?: KColors.WHITE }
                    text("wins.") { color = KColors.GRAY }
                }
            }
        }
    }

    override fun onEnd() {
        Bukkit.shutdown()
    }
}