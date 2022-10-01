package de.hglabor.bunkers.game.pvpstyle

import de.hglabor.common.extension.broadcast
import de.hglabor.hcfcore.Manager
import net.axay.kspigot.chat.KColors
import org.bukkit.entity.Player
import java.util.*

class PvPStyleManager {
    val allStyles: MutableSet<IPvPStyle> = mutableSetOf()
    private val votes: MutableMap<UUID, IPvPStyle> = mutableMapOf()
    var style: IPvPStyle? = null

    fun vote(player: Player, pvpStyle: IPvPStyle) {
        votes[player.uniqueId] = pvpStyle
    }

    fun getVotes(pvpStyle: IPvPStyle): Int {
        return votes.values.count { it == pvpStyle }
    }

    fun register(pvpStyle: IPvPStyle) {
        allStyles.add(pvpStyle)
        Manager.logger.info("PvPStyle ${pvpStyle.name} has been registered")
    }

    fun selectFromPool() {
        val s = votes.values.randomOrNull() ?: allStyles.random()
        style = s
        broadcast {
            text("The game is played in ")
            text("${s.name} ") { color = KColors.RED }
            text("style.")
            bold = true
        }
        s.enable()
    }
}