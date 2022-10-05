package de.hglabor.common.playerlist.body

import de.hglabor.common.extension.connection
import de.hglabor.common.extension.serverPlayer
import de.hglabor.common.playerlist.SkinTexture
import de.hglabor.hcfcore.Manager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.onlinePlayers
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.PluginDisableEvent
import java.util.*
import kotlin.time.Duration.Companion.seconds

class PlayerListBody {
    companion object {
        val lists = mutableSetOf<PlayerListBody>()

        init {
            val job = CoroutineScope(Dispatchers.IO).launch {
                while (true) {
                    lists.filter { it.shownTo.isNotEmpty() }.forEach { playerlist ->
                        playerlist.updateNamesAndSkins()
                    }
                    delay(1.seconds)
                }
            }

            listen<PluginDisableEvent> {
                if (it.plugin == Manager) {
                    job.cancel()
                }
            }

            listen<PlayerQuitEvent> {
                lists.find { list -> it.player.uniqueId in list.shownTo }?.hide(it.player)
            }
        }
    }

    private val shownTo = mutableSetOf<UUID>()
    private val entries = Array(4) { x ->
        Array(20) { y ->
            PlayerListEntry(x, y)
        }
    }

    /**
     * returns the [PlayerListEntry] at the given coordinates
     *
     * @param x the index of the column the entry is in
     * @param y the index of the line the entry is in
     */
    fun getEntry(x: Int, y: Int): PlayerListEntry {
        return entries[x][y]
    }

    /**
     * updates an entry to have a new name and skin
     *
     * @param x the index of the column the entry is in
     * @param y the index of the line the entry is in
     * @param skinTexture the skin that will be set
     * @param textCallback the callback that will be used to invoke the text, this will update every second
     */
    fun setEntry(x: Int, y: Int, skinTexture: SkinTexture? = null, textCallback: (() -> MutableComponent)? = null) {
        getEntry(x, y).let { entry ->
            if (skinTexture != null) entry.setSkin(skinTexture)
            if (textCallback != null) entry.setText(textCallback)
        }
    }

    /**
     * updates an entry to have a new name and skin
     *
     * @param x the index of the column the entry is in
     * @param y the index of the line the entry is in
     * @param skinTextureCallback the callback that will be invoked to get the skin, this will update every second
     * @param text the callback that will be used to invoke the text
     */
    fun setEntry(x: Int, y: Int, skinTextureCallback: (() -> SkinTexture)? = null, text: MutableComponent? = null) {
        getEntry(x, y).let { entry ->
            if (skinTextureCallback != null) entry.setSkin(skinTextureCallback)
            if (text != null) entry.setText(text)
        }
    }

    /**
     * updates an entry to have a new name and skin
     *
     * @param x the index of the column the entry is in
     * @param y the index of the line the entry is in
     * @param skinTexture the skin that will be set
     * @param text the component that will be set as the text of the line
     */
    fun setEntry(x: Int, y: Int, skinTexture: SkinTexture? = null, text: MutableComponent? = null) {
        getEntry(x, y).let { entry ->
            if (skinTexture != null) entry.setSkin(skinTexture)
            if (text != null) entry.setText(text)
        }
    }

    /**
     * updates an entry to have a new name and skin
     *
     * @param x the index of the column the entry is in
     * @param y the index of the line the entry is in
     * @param skinTextureCallback the callback that will be invoked to get the skin, this will update every second
     * @param textCallback textCallback the callback that will be used to invoke the text, this will update every second
     */
    fun setEntry(x: Int, y: Int, skinTextureCallback: (() -> SkinTexture)? = null, textCallback: (() -> MutableComponent)? = null) {
        getEntry(x, y).let { entry ->
            if (skinTextureCallback != null) entry.setSkin(skinTextureCallback)
            if (textCallback != null) entry.setText(textCallback)
        }
    }

    /**
     * adds all [PlayerListEntry]s to a player's tablist
     *
     * Note: You should always hide the previous body before showing a new one
     *
     * @param player the player that will receive the packets
     */
    fun show(player: Player) {
        if (this !in lists) lists.add(this)
        lists.find { player.uniqueId in it.shownTo }?.hide(player)

        shownTo.add(player.uniqueId)
        onlinePlayers.forEach { onlinePlayer ->
            player.connection.send(
                ClientboundPlayerInfoPacket(
                    ClientboundPlayerInfoPacket.Action.ADD_PLAYER,
                    onlinePlayer.serverPlayer
                )
            )
        }

        entries.forEach { column ->
            column.forEach { entry ->
                player.connection.send(
                    ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, entry.serverPlayer)
                )
            }
        }
    }

    /**
     * hides all [PlayerListEntry]s from a player's tablist
     *
     * @param player the player that will receive the packets
     */
    fun hide(player: Player) {
        shownTo.remove(player.uniqueId)
        if (this in lists && shownTo.isEmpty()) lists.remove(this)

        entries.forEach { column ->
            column.forEach { entry ->
                player.connection.send(
                    ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, entry.serverPlayer)
                )
            }
        }
    }

    /**
     * updates the names and skins of the [PlayerListEntry]s
     */
    fun updateNamesAndSkins() {
        entries.forEach { column ->
            column.forEach { entry ->
                shownTo.mapNotNull { Bukkit.getPlayer(it) }.forEach { player ->
                    if (entry.shouldUpdateName) entry.updateName(player)
                    if (entry.shouldUpdateSkin) entry.updateSkin(player)
                }
            }
        }
    }
}