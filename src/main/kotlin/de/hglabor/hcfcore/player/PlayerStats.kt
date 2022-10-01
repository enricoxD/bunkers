package de.hglabor.hcfcore.player

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.math.RoundingMode

@Serializable
class PlayerStats {
    var kills: Int = 0
    var deaths: Int = 0
    var killstreak: Int = 0

    val kdr get() = BigDecimal(kills / deaths).setScale(2, RoundingMode.HALF_UP).toDouble()
}