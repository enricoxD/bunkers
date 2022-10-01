package de.hglabor.common.playerlist.body

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import de.hglabor.common.extension.connection
import de.hglabor.common.playerlist.SkinColor
import de.hglabor.common.text.literalText
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import org.bukkit.entity.Player
import java.util.*

/**
 * Creates a [PlayerListEntry] which will update every second.
 *
 * @param skin the [SkinColor] you want the entry to have
 * @param textCallback the callback that will be invoked to get a new text on update
 */
class PlayerListEntry(val skin: SkinColor, var textCallback: () -> MutableComponent) {
    companion object {
        private val SERVER = MinecraftServer.getServer()
    }

    /**
     * Creates a [PlayerListEntry] which will not update automatically.
     *
     * @param skin the [SkinColor] you want the entry to have
     * @param component the component which will be displayed in the tablist
     */
    constructor(skin: SkinColor, component: MutableComponent) : this(skin, { component }) {
        shouldUpdate = false
    }

    constructor(skin: SkinColor) : this(skin, { literalText() }) {
        shouldUpdate = false
    }

    var shouldUpdate = true
    var forceUpdate = false

    val serverPlayer = ServerPlayer(
        SERVER,
        SERVER.overworld(),
        GameProfile(UUID.randomUUID(), ""),
        null
    )

    /**
     * actually updates the name of the [PlayerListEntry.serverPlayer]
     *
     * @param player the player that will receive the packet
     */
    fun updateName(player: Player) {
        serverPlayer.javaClass.getDeclaredField("listName").apply {
            isAccessible = true
            set(serverPlayer, textCallback())
            isAccessible = true
        }
        player.connection.send(
            ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.UPDATE_DISPLAY_NAME, serverPlayer)
        )
    }

    /**
     * changes the skin of the entry
     *
     * note: You will need to hide the entry once.
     * @see [PlayerListColumn.hide]
     **/
    fun setSkin(skin: SkinColor) {
        serverPlayer.gameProfile.properties.apply {
            removeAll("textures")
            put("textures", Property("textures", skin.texture, skin.signature))
        }
    }

    /**
     * changes the text of the entry
     * marks the entry to be updated automatically
     *
     * @param textCallback the callback that will be used to update the displayed component
     */
    fun set(textCallback: () -> MutableComponent) {
        this.textCallback = textCallback
        shouldUpdate = true
        forceUpdate = false
    }

    /**
     * changes the text of the entry
     * marks the entry to not be updated
     *
     * @param component the component that will be displayed in the tablist
     */
    fun set(component: MutableComponent) {
        this.textCallback = { component }
        shouldUpdate = false
        forceUpdate = true
    }
}