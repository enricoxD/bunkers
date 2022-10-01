package de.hglabor.bunkers.game.phase

import de.hglabor.bunkers.game.GameManager
import de.hglabor.hcfcore.Manager
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener

abstract class GamePhase(val maxDuration: Int, val nextPhase: GamePhase?): Listener {
    val remainingTime get() = maxDuration - GameManager.elapsedTime

    open fun onStart() {
        Manager.server.pluginManager.registerEvents(this, Manager)
    }
    open fun onEnd() {
        HandlerList.unregisterAll(this)
    }

    open fun onTick() {
        GameManager.elapsedTime += 1
    }
}