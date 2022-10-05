package de.hglabor.common.playerlist.body

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import de.hglabor.common.extension.connection
import de.hglabor.common.playerlist.SkinTexture
import de.hglabor.common.text.literalText
import de.hglabor.common.utils.UpdatingProperty
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import org.bukkit.entity.Player
import java.util.*

/**
 * Creates a [PlayerListEntry] which will update every second.
 *
 * @param x the index of the column the entry will be in
 * @param y the index of the line the entry will be in
 */
class PlayerListEntry(val x: Int, val y: Int) {
    companion object {
        private val SERVER = MinecraftServer.getServer()
    }

    private var Name = UpdatingProperty(literalText())
    private val Skin = UpdatingProperty<SkinTexture>(SkinTexture.DARK_GRAY)

    val serverPlayer = ServerPlayer(
        SERVER,
        SERVER.overworld(),
        GameProfile(UUID.randomUUID(), String.format("%02d", x * 20 + y)),
        null
    )

    val shouldUpdateName get() = Name.shouldUpdate || Name.forceUpdate
    val shouldUpdateSkin get() = Skin.shouldUpdate || Skin.forceUpdate

    /**
     * sends the packet to update the name of the [PlayerListEntry.serverPlayer]
     *
     * @param player the player that will receive the packet
     */
    fun updateName(player: Player) {
        val name = Name.get()

        serverPlayer.javaClass.getDeclaredField("listName").apply {
            isAccessible = true
            set(serverPlayer, name)
            isAccessible = false
        }

        player.connection.send(
            ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.UPDATE_DISPLAY_NAME, serverPlayer)
        )
    }

    /**
     * sends the packets to update the skin of the [PlayerListEntry.serverPlayer]
     *
     * @param player the player that will receive the packet
     */
    fun updateSkin(player: Player) {
        val skin = Skin.get()

        serverPlayer.gameProfile.properties.apply {
            val oldTextures = get("textures")
            if (oldTextures.isNotEmpty() && oldTextures.first().value == skin.texture) return
            removeAll("textures")
            put("textures", Property("textures", skin.texture, skin.signature))
        }

        player.connection.send(
            ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, serverPlayer)
        )
        player.connection.send(
            ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, serverPlayer)
        )
    }

    /**
     * changes the text of the entry
     * marks the entry's text to be updated automatically
     *
     * @param callback the callback that will be used to update the displayed component
     */
    fun setText(callback: () -> MutableComponent) {
        Name.set(callback)
    }

    /**
     * changes the text of the entry
     * marks the entry's text to not be updated
     *
     * @param component the component that will be displayed in the tablist
     */
    fun setText(component: MutableComponent) {
        Name.set(component)
    }

    /**
     * changes the skin of the entry
     * marks the entry's text to be updated automatically
     *
     * @param callback the callback that will be used to update the displayed component
     */
    fun setSkin(callback: () -> SkinTexture) {
        Skin.set(callback)
    }

    /**
     * changes the skin of the entry
     * marks the entry's text to not be updated
     *
     * @param newSkin the new [SkinTexture] that will be displayed in the tablist
     */
    fun setSkin(newSkin: SkinTexture) {
        Skin.set(newSkin)
    }
}