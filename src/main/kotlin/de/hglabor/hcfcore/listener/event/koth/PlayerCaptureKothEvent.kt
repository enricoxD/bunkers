package de.hglabor.hcfcore.listener.event.koth

import de.hglabor.hcfcore.event.koth.Koth
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PlayerCaptureKothEvent(val player: Player, val koth: Koth) : Event() {
    companion object {
        @JvmStatic
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = HANDLERS
    }

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }
}