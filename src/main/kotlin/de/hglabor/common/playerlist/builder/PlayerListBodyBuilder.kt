package de.hglabor.common.playerlist.builder

import de.hglabor.common.playerlist.body.PlayerListBody

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

    operator fun (PlayerListColumnBuilder.() -> Unit).unaryPlus() {
        val index = body.columns.indexOfFirst { it == null }
        if (index == -1) throw IndexOutOfBoundsException()

        val column = PlayerListColumnBuilder(index).apply(this).column
        body.addColumn(column)
    }
    operator fun set(index: Int, entryCallback: PlayerListColumnBuilder.() -> Unit) {
        val column = PlayerListColumnBuilder(index).apply(entryCallback).column
        body.addColumn(index, column)
    }
}