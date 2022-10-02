package de.hglabor.common.playerlist.builder

import de.hglabor.common.playerlist.body.PlayerListBody

class PlayerListBodyBuilder(val body: PlayerListBody) {
    // TODO Idk if its worth but maybe add an builder to create the placeholders
    /*fun placeholder(callback: PlayerListEntryBuilder.() -> Unit) {
        body.placeholderCallback = callback
    }*/

    fun column(index: Int, callback: PlayerListColumnBuilder.() -> Unit): Pair<Int, PlayerListColumnBuilder.() -> Unit> {
        return index to callback
    }

    operator fun (Pair<Int, PlayerListColumnBuilder.() -> Unit>).unaryPlus() {
        val (columnIndex, builder) = this
        builder.invoke(PlayerListColumnBuilder(body, columnIndex))
    }
}