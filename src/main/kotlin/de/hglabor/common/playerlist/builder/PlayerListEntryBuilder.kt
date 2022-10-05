package de.hglabor.common.playerlist.builder

import de.hglabor.common.playerlist.SkinTexture
import de.hglabor.common.playerlist.body.PlayerListBody
import de.hglabor.common.text.LiteralTextBuilder
import net.axay.kspigot.extensions.broadcast
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style

class PlayerListEntryBuilder(val body: PlayerListBody, val x: Int, val y: Int) {
    private val entry = body.getEntry(x, y)

    fun name(text: MutableComponent) {
        entry.setText(text)
    }

    fun name(builder: LiteralTextBuilder.() -> Unit) {
        entry.setText { LiteralTextBuilder("", Style.EMPTY, false).apply(builder).build() }
    }

    fun skin(skin: SkinTexture) {
        entry.setSkin(skin)
    }

    fun skin(callback: () -> SkinTexture) {
        entry.setSkin(callback)
    }
}