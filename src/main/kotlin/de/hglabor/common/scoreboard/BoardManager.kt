package de.hglabor.common.scoreboard

import de.hglabor.auseinandersetzung.common.scoreboard.Board
import de.hglabor.auseinandersetzung.common.scoreboard.BoardBuilder
import de.hglabor.auseinandersetzung.common.scoreboard.set
import de.hglabor.common.scoreboard.ScoreboardManager.boards
import org.bukkit.entity.Player
import java.util.*

object ScoreboardManager {
    val boards = hashMapOf<UUID, Board>()

    fun remove(uuid: UUID) {
        boards.remove(uuid)?.delete()
    }
}

inline fun Player.updateScoreboard(crossinline builder: BoardBuilder.() -> Unit) {
    boards.computeIfAbsent(uniqueId) { Board().setScoreboard(this) }.set {
        builder.invoke(this)
    }
}