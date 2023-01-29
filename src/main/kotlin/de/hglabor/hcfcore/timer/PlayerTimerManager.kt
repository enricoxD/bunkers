package de.hglabor.hcfcore.timer

import java.util.UUID

object PlayerTimerManager {
    val playerCooldowns = mutableMapOf<UUID, AbstractPlayerTimer>()
}