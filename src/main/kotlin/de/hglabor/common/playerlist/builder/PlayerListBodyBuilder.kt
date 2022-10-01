package de.hglabor.common.playerlist.builder

import de.hglabor.auseinandersetzung.common.scoreboard.BoardBuilder
import de.hglabor.common.playerlist.body.PlayerListBody
import de.hglabor.common.playerlist.body.PlayerListColumn

class PlayerListBodyBuilder(val body: PlayerListBody = PlayerListBody()) {
    var removePlayers = false
        set(value) {
            body.removePlayers = value
            field = value
        }

    fun placeholder(callback: PlayerListEntryBuilder.() -> Unit) {
        body.placeholderCallback = callback
    }

    fun column(callback: PlayerListColumnBuilder.() -> Unit): PlayerListColumnBuilder.() -> Unit {
        return callback
    }

    operator fun PlayerListColumn.unaryPlus() {
        body.addColumn(this)
    }

    operator fun (PlayerListColumnBuilder.() -> Unit).unaryPlus() {
        val column = PlayerListColumnBuilder().apply(this).column
        body.addColumn(column)
    }

    operator fun set(index: Int, column: PlayerListColumn) {
        body.addColumn(index, column)
    }

    operator fun set(index: Int, entryCallback: PlayerListColumnBuilder.() -> Unit) {
        val column = PlayerListColumnBuilder().apply(entryCallback).column
        body.addColumn(index, column)
    }
}