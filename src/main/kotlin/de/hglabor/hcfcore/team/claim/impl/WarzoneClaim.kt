package de.hglabor.hcfcore.team.claim.impl

import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.team.claim.IClaim
import de.hglabor.hcfcore.team.claim.Region
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World

@Serializable
class WarzoneClaim(
    @Transient
    val worldName: String = "world",
    val radius: Int,
    override var name: String,
): IClaim {
    val world: World by lazy { Bukkit.getWorld(worldName) ?: error("Couldn't get world '$worldName'") }

    override val region = Region(
        Location(world, radius.toDouble(), 0.0, radius.toDouble()),
        Location(world, -radius.toDouble(), 0.0, -radius.toDouble())
    )

    override fun unclaim() {
        Core.teamManager.teamByName(name)?.disband()
    }
}