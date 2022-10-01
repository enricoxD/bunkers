package de.hglabor.bunkers.shop.entry

import de.hglabor.hcfcore.player.impl.TeamPlayer
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component

@Serializable
class RightClickAction(
    val description: Component,
    val onRightClick: ((TeamPlayer) -> Unit)
)