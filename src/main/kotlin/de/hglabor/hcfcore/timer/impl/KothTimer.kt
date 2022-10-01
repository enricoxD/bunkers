package de.hglabor.hcfcore.timer.impl

import de.hglabor.hcfcore.event.koth.KothManager
import de.hglabor.hcfcore.listener.event.koth.PlayerCaptureKothEvent
import de.hglabor.hcfcore.timer.ITimer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.extensions.broadcast
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class KothTimer(override val name: String, private val duration: Int) : ITimer {
    override var start: Long? = null

    override fun remainingTime(): Long {
        val start = start ?: return duration * 1000L
        val end = start + (duration * 1000L)
        return end - System.currentTimeMillis()
    }

    override fun onEnd() {
        val koth = KothManager.currentKoth ?: return
        val king = koth.currentKing
        if (king != null) {
            Bukkit.getPluginManager().callEvent(PlayerCaptureKothEvent(king.player!!, koth))
            broadcast(literalText {
                component(KothManager.kothPrefix)
                text("${king.teamName} ") { color = KColors.AQUAMARINE }
                text("has ") { color = KColors.GRAY }
                text("controlled ") { color = KColors.GREEN }
                text(koth.team.name) { color = KColors.DARKRED }
                text(".") { color = KColors.GRAY }
            })
        } else {
            broadcast(literalText {
                component(KothManager.kothPrefix)
                text("KOTH ") { color = KColors.GRAY }
                text("${koth.team.name} ") { color = KColors.DARKRED }
                text("has been ") { color = KColors.GRAY }
                text("cancelled") { color = KColors.RED }
                text(".") { color = KColors.GRAY }
            })
        }
    }
}