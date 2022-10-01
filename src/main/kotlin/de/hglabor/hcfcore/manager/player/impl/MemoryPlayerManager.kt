package de.hglabor.hcfcore.manager.player.impl

import de.hglabor.hcfcore.manager.player.IPlayerManager
import de.hglabor.hcfcore.player.impl.TeamPlayer
import java.util.*

open class MemoryPlayerManager: IPlayerManager {
    override val cache: MutableMap<UUID, TeamPlayer> = mutableMapOf()

    override fun disable() {}
}