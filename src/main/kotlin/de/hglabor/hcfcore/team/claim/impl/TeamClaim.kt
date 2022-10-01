package de.hglabor.hcfcore.team.claim.impl

import de.hglabor.common.serialization.UUIDSerializer
import de.hglabor.hcfcore.team.claim.IClaim
import de.hglabor.hcfcore.team.claim.Region
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
class TeamClaim(
    val accessibleMembers: MutableSet<@Serializable(with = UUIDSerializer::class) UUID>,
    override var region: Region,
    override var name: String
): IClaim