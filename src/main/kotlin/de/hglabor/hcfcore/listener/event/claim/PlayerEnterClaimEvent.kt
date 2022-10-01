package de.hglabor.hcfcore.listener.event.claim

import de.hglabor.hcfcore.team.claim.IClaim
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PlayerEnterClaimEvent(val player: Player, val claim: IClaim) : Event(true) {
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