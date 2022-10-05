package de.hglabor.hcfcore.listener.event.team

import de.hglabor.hcfcore.team.impl.PlayerTeam
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PlayerLeaveTeamEvent(val player: Player, val team: PlayerTeam) : Event() {
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