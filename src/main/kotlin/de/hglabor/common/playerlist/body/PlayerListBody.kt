package de.hglabor.common.playerlist.body

import de.hglabor.common.extension.connection
import de.hglabor.common.extension.serverPlayer
import de.hglabor.common.playerlist.PlayerListManager
import de.hglabor.common.playerlist.builder.PlayerListEntryBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.axay.kspigot.extensions.onlinePlayers
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket
import net.minecraft.network.protocol.game.ClientboundTabListPacket
import net.minecraft.world.scores.Scoreboard
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

class PlayerListBody {
    companion object {
        val lists = mutableMapOf<UUID, PlayerListBody>()
    }

    var columns = arrayOfNulls<PlayerListColumn>(PlayerListManager.MAX_COLUMNS)
    var removePlayers = false
    var placeholderCallback: (PlayerListEntryBuilder.() -> Unit) = { }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                lists.filter { it.value == this@PlayerListBody }.keys.forEach { uuid ->
                    val player = Bukkit.getPlayer(uuid) ?: return@forEach
                    updateNames(player)
                }
                delay(1.seconds)
            }
        }
    }

    /**
     * adds a [PlayerListColumn] to the body
     *
     * @param index the index where the column will be added
     * @param column the column that will be added
     */
    fun addColumn(index: Int, column: PlayerListColumn) {
        columns[index] = column
    }

    /**
     * adds a [PlayerListColumn] to the column
     *
     * @param column the column that will be added
     */
    fun addColumn(column: PlayerListColumn) {
        if (columns.none { it == null }) {
            throw IllegalArgumentException("A PlayerListBody can't have more than ${PlayerListManager.MAX_COLUMNS} columns")
        }

        columns.indexOfFirst { _entry -> _entry == null }.also { index ->
            columns[index] = column
        }
    }

    /**
     * removes a [PlayerListColumn] from the body
     *
     * Note: You should always hide the column before removing it
     *
     * @param index the index of the column to be removed
     */
    fun removeColumn(index: Int) {
        columns[index] = null
    }

    /**
     * removes a [PlayerListColumn] from the body
     *
     * Note: You should always hide the column before removing it
     *
     * @param column the entry that will be removed
     */
    fun removeColumn(column: PlayerListColumn) {
        if (column !in columns) return
        columns.indexOf(column).also { index ->
            columns[index] = null
        }
    }

    /**
     * shows all [PlayerListEntry]s within the [PlayerListColumn]s to a player
     *
     * Note: You should always hide the body before showing it again
     *
     * @param player the player that will receive the packets
     */
    fun show(player: Player) {
        lists[player.uniqueId] = this
        if (removePlayers) {
            onlinePlayers.forEach { onlinePlayer ->
                player.connection.send(
                    ClientboundPlayerInfoPacket(
                        ClientboundPlayerInfoPacket.Action.ADD_PLAYER,
                        onlinePlayer.serverPlayer
                    )
                )
            }
        }

        columns.filterNotNull().forEachIndexed { x, column ->
            column.entries.forEachIndexed { y, _entry ->
                val entry = _entry ?: (PlayerListEntryBuilder(x, y).apply(placeholderCallback).entry).also {
                    column.entries[y] = it
                }

                player.connection.send(
                    ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, entry.serverPlayer)
                )

                val team = net.minecraft.world.scores.PlayerTeam(Scoreboard(), teamName(x, y))
                player.connection.send(
                    ClientboundSetPlayerTeamPacket.createPlayerPacket(
                        team,
                        "",
                        ClientboundSetPlayerTeamPacket.Action.ADD
                    ),
                )
            }
        }
    }

    /**
     * hides all [PlayerListEntry]s within the [PlayerListColumn]s from a player's tablist
     *
     * @param player the player that will receive the packets
     */
    fun hide(player: Player) {
        lists.remove(player.uniqueId)
        columns.filterNotNull().forEach { column ->
            column.entries.forEach { _entry ->
                val entry = _entry ?: return@forEach

                player.connection.send(
                    ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, entry.serverPlayer)
                )
            }
        }
    }

    /**
     * updates the name of every [PlayerListEntry] in a column
     *
     * @param player the player that will receive the packet
     */
    fun updateNames(player: Player) {
        columns.filterNotNull().forEach { column ->
            column.entries
                .filterNotNull()
                .filter { entry -> entry.shouldUpdate || entry.forceUpdate }
                .forEach { entry ->
                    entry.updateName(player)
                    entry.forceUpdate = false
                }
        }
    }

    abstract class Jop() {

    }
}

class Dasd(): PlayerListBody.Jop()
private fun teamName(x: Int, y: Int): String {
    return "$x.${String.format("%02d", y)}"
}