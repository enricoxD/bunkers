package de.hglabor.common.playerlist.builder

import de.hglabor.common.playerlist.PlayerListManager
import de.hglabor.common.playerlist.body.PlayerListColumn

class PlayerListColumnBuilder(val x: Int, val column: PlayerListColumn = PlayerListColumn(PlayerListManager.MAX_ENTRIES_PER_COLUMN)) {

    operator fun (PlayerListEntryBuilder.() -> Unit).unaryPlus() {
        val index = column.entries.indexOfFirst { it == null }
        if (index == -1) throw IndexOutOfBoundsException()

        val entry = PlayerListEntryBuilder(x, index).apply(this).entry
        column.addEntry(entry)
    }

    operator fun Pair<Int, (PlayerListEntryBuilder.() -> Unit)>.unaryPlus() {
        val entry = PlayerListEntryBuilder(x, this.first).apply(this.second).entry
        column.addEntry(this.first, entry)
    }

    fun entry(callback: PlayerListEntryBuilder.() -> Unit): PlayerListEntryBuilder.() -> Unit {
        return callback
    }
}