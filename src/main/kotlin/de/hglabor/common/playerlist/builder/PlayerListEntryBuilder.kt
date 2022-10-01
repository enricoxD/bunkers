package de.hglabor.common.playerlist.builder

import de.hglabor.common.playerlist.SkinColor
import de.hglabor.common.playerlist.body.PlayerListEntry
import net.minecraft.network.chat.MutableComponent

class PlayerListEntryBuilder(val entry: PlayerListEntry = PlayerListEntry(SkinColor.WHITE)) {
    var name: () -> MutableComponent = entry.textCallback
        set(value) {
            entry.textCallback = value
            entry.forceUpdate = true
            field = value
        }

    var shouldUpdate: Boolean = entry.shouldUpdate
        set(value) {
            entry.shouldUpdate = value
            field = value
        }

    var skin: SkinColor = entry.skin
        set(value) {
            entry.setSkin(value)
            field = value
        }
}