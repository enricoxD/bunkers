package de.hglabor.bunkers.game

import de.hglabor.bunkers.game.phase.GamePhase
import de.hglabor.bunkers.game.phase.phases.LobbyPhase
import de.hglabor.bunkers.game.pvpstyle.PvPStyleManager
import de.hglabor.common.playerlist.builder.PlayerListManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import kotlin.time.Duration.Companion.seconds

object GameManager {
    var elapsedTime = 0
    var currentPhase: GamePhase = LobbyPhase
    var pvpStyleManager: PvPStyleManager = PvPStyleManager()

    fun enable() {
        currentPhase.onStart()
        PlayerListManager.enable()
        CoroutineScope(Dispatchers.IO).launch {
            startTimer()
        }
    }

    fun startNextPhase() {
        currentPhase.onEnd()
        val nextPhase = currentPhase.nextPhase
        elapsedTime = 0
        if (nextPhase == null) {
            Bukkit.shutdown()
            return
        }
        currentPhase = nextPhase
        nextPhase.onStart()
    }

    private suspend fun startTimer() {
        while (true) {
            if (elapsedTime > currentPhase.maxDuration) {
                startNextPhase()
            }
            delay(1.seconds)
            currentPhase.onTick()
        }
    }
}