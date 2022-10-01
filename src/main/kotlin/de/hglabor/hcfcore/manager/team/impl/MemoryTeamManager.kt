package de.hglabor.hcfcore.manager.team.impl

import de.hglabor.hcfcore.team.ITeam
import de.hglabor.hcfcore.manager.team.ITeamManager

open class MemoryTeamManager : ITeamManager {
    override val cache: MutableMap<String, ITeam> = mutableMapOf()

    override fun disable() {}
}