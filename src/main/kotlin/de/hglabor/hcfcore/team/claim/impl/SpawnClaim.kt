package de.hglabor.hcfcore.team.claim.impl

import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.team.claim.IOverlappingClaim
import de.hglabor.hcfcore.team.claim.Region
import kotlinx.serialization.Serializable

@Serializable
class SpawnClaim(
    override var name: String,
    override val region: Region
) : IOverlappingClaim {

    override fun unclaim() {
        Core.teamManager.teamByName(name)?.disband()
    }
}
