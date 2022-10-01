package de.hglabor.common.playerlist

import de.hglabor.common.playerlist.body.PlayerListBody
import org.bukkit.entity.Player
import java.util.*

object PlayerListManager {
    const val MAX_ENTRIES_PER_COLUMN = 20
    const val MAX_COLUMNS = 4

    val playerLists = hashMapOf<UUID, PlayerListBody>()

    fun remove(player: Player) {
        playerLists.remove(player.uniqueId)?.hide(player)
    }
}