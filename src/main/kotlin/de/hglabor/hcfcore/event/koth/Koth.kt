package de.hglabor.hcfcore.event.koth

import de.hglabor.hcfcore.team.impl.KothTeam
import de.hglabor.hcfcore.manager.player.teamPlayer
import de.hglabor.hcfcore.player.impl.TeamPlayer
import de.hglabor.hcfcore.timer.impl.KothTimer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import org.bukkit.entity.Player

class Koth(
    val team: KothTeam,
    val timer: KothTimer
) {
    var currentKing: TeamPlayer? = null

    fun startCapturing(player: Player) {
        currentKing = player.teamPlayer
        timer.start = System.currentTimeMillis()
        player.sendMessage(literalText {
            component(KothManager.kothPrefix)
            text("You are now ")
            text("controlling ") { color = KColors.GREEN }
            text(team.name) { color = KColors.DARKRED }
            text("!")
        })
    }

    fun stopCapturing() {
        currentKing = null
        timer.start = null

        val king = currentKing?.player ?: return
        king.sendMessage(literalText {
            component(KothManager.kothPrefix)
            text("Lost ") { color = KColors.RED }
            text("control of ")
            text(team.name) { color = KColors.DARKRED }
            text("!")
        })
    }
}