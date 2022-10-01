package de.hglabor.common.playerlist.body

import de.hglabor.common.playerlist.PlayerListManager

class PlayerListColumn(val size: Int): PlayerListBody.Jop() {
    init {
        if (size > PlayerListManager.MAX_ENTRIES_PER_COLUMN)
            throw IllegalArgumentException("PlayerListColumns can't have more than ${PlayerListManager.MAX_ENTRIES_PER_COLUMN} entries")
    }

    val entries = arrayOfNulls<PlayerListEntry>(size)

    /**
     * adds an [PlayerListEntry] to the column
     *
     * @param index the index where the entry will be added
     * @param entry the entry that will be added
     */
    fun addEntry(index: Int, entry: PlayerListEntry) {
        entries[index] = entry
    }

    /**
     * adds a [PlayerListEntry] to the column
     *
     * @param entry the entry that will be added
     */
    fun addEntry(entry: PlayerListEntry) {
        if (entries.none { it == null }) {
            throw IllegalArgumentException("PlayerListColumns can't have more than ${PlayerListManager.MAX_ENTRIES_PER_COLUMN} entries")
        }

        entries.indexOfFirst { _entry -> _entry == null }.also { index ->
            entries[index] = entry
        }
    }

    /**
     * removes a [PlayerListEntry] from the column
     *
     * Note: You should always hide the column before removing an entry
     *
     * @param index the index of the entry to be removed
     */
    fun removeEntry(index: Int) {
        entries[index] = null
    }

    /**
     * removes a [PlayerListEntry] from the column
     *
     * Note: You should always hide the column before removing a entry
     *
     * @param entry the entry that will be removed
     */
    fun removeEntry(entry: PlayerListEntry) {
        if (entry !in entries) return
        entries.indexOf(entry).also { index ->
            entries[index] = null
        }
    }
}