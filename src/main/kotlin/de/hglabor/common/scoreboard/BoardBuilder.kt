package de.hglabor.common.scoreboard

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class BoardBuilder(val board: Board) {
    var lineBuilder = LineBuilder()

    var title: Component
        set(value) {
            board.title = value
        }
        get() = board.title

    var period: Long
        set(value) {
            board.updatingPeriod = value
        }
        get() = board.updatingPeriod

    inline fun content(crossinline callback: LineBuilder.() -> Unit) {
        lineBuilder = LineBuilder().apply(callback)
    }

    fun addFlag(boardFlag: BoardFlag) {
        board.addFlag(boardFlag)
    }

    fun invoke(reverse: Boolean) {
        if (reverse) reverseLines()
        board.lines.forEach { it.register() }
    }

    private fun reverseLines() {
        board.lines.reverse()
    }

    inner class LineBuilder {
        operator fun Component.unaryPlus() {
            board.lines += board.BoardLine(this)
        }

        operator fun (() -> Component).unaryPlus() {
            board.lines += board.BoardLine(this)
        }
    }
}

inline fun Player.setScoreboard(updatingPeriod: Long = 20, bottomToTop: Boolean = true, crossinline builder: BoardBuilder.() -> Unit): Board {
    return Board(updatingPeriod).apply {
        BoardBuilder(this).apply(builder).invoke(bottomToTop)
    }.setScoreboard(this).also { board ->
        ScoreboardManager.boards[uniqueId] = board
    }
}

inline fun Board(updatingPeriod: Long = 20, bottomToTop: Boolean = true, crossinline builder: BoardBuilder.() -> Unit): Board {
    return Board(updatingPeriod).apply {
        BoardBuilder(this).apply(builder).invoke(bottomToTop)
    }
}

inline fun Board.set(updatingPeriod: Long = 20, bottomToTop: Boolean = true, crossinline builder: BoardBuilder.() -> Unit): Board {
    return this.apply {
        this.updatingPeriod = updatingPeriod
        clear()
        BoardBuilder(this).apply(builder).invoke(bottomToTop)
        updateBoard()
    }
}

fun Player.setScoreboard(board: Board) {
    ScoreboardManager.boards[uniqueId] = board
    board.setScoreboard(this)
}
