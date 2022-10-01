package de.hglabor.common.playerlist.builder

import de.hglabor.common.playerlist.PlayerListManager
import de.hglabor.common.playerlist.body.PlayerListColumn
import de.hglabor.common.playerlist.body.PlayerListEntry

class PlayerListColumnBuilder(val column: PlayerListColumn = PlayerListColumn(PlayerListManager.MAX_ENTRIES_PER_COLUMN)) {
    operator fun PlayerListEntry.unaryPlus() {
        column.addEntry(this)
    }

    operator fun (PlayerListEntryBuilder.() -> Unit).unaryPlus() {
        val entry = PlayerListEntryBuilder().apply(this).entry
        column.addEntry(entry)
    }

    operator fun set(index: Int, entry: PlayerListEntry) {
        column.addEntry(index, entry)
    }

    operator fun set(index: Int, entryCallback: PlayerListEntryBuilder.() -> Unit) {
        val entry = PlayerListEntryBuilder().apply(entryCallback).entry
        column.addEntry(index, entry)
    }

    fun entry(callback: PlayerListEntryBuilder.() -> Unit): PlayerListEntryBuilder.() -> Unit {
        return callback
    }
}