package de.hglabor.common.playerlist.builder

import de.hglabor.common.playerlist.body.PlayerListBody

class PlayerListColumnBuilder(private val body: PlayerListBody, private val x: Int) {

    fun entry(index: Int, callback: PlayerListEntryBuilder.() -> Unit): Pair<Int, PlayerListEntryBuilder.() -> Unit> {
        return index to callback
    }

    operator fun Pair<Int, (PlayerListEntryBuilder.() -> Unit)>.unaryPlus() {
        val (y, builder) = this
        PlayerListEntryBuilder(body, x, y).apply(builder)
    }
}