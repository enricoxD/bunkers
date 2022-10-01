package de.hglabor.hcfcore.team.data

import kotlinx.serialization.Serializable
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.kyori.adventure.text.Component

@Serializable
enum class RegenStatus(val icon: Component) {
    FULL(literalText("\u25B6") { color = KColors.LIME }),
    REGENERATING(literalText("\u21ea") { color = KColors.YELLOW }),
    PAUSED(literalText("\u25a0") { color = KColors.RED }),
}