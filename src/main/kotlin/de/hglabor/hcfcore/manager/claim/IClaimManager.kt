package de.hglabor.hcfcore.manager.claim

import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.configuration.TConfig
import de.hglabor.hcfcore.listener.event.claim.PlayerEnterClaimEvent
import de.hglabor.hcfcore.listener.event.claim.PlayerLeaveClaimEvent
import de.hglabor.hcfcore.team.ITeam
import de.hglabor.hcfcore.team.claim.IClaim
import de.hglabor.hcfcore.team.claim.IOverlappingClaim
import de.hglabor.hcfcore.team.claim.Region
import de.hglabor.hcfcore.team.claim.selection.AbstractClaimSelection
import de.hglabor.hcfcore.team.claim.selection.ClaimSelectionManager
import de.hglabor.hcfcore.team.claim.claimwand.ClaimWandManager
import de.hglabor.hcfcore.team.claim.impl.TeamClaim
import de.hglabor.hcfcore.team.claim.impl.KothClaim
import de.hglabor.hcfcore.team.claim.impl.SpawnClaim
import de.hglabor.hcfcore.team.impl.PlayerTeam
import de.hglabor.hcfcore.manager.IManager
import de.hglabor.hcfcore.manager.player.teamPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.extensions.geometry.add
import net.axay.kspigot.extensions.geometry.withWorld
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.runnables.KSpigotRunnable
import net.axay.kspigot.runnables.task
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import kotlin.math.roundToInt

interface IClaimManager: IManager<String, IClaim> {
    val claimSelectionManager: ClaimSelectionManager
    var playerChangeClaimTask: KSpigotRunnable?

    override fun enable() {
        ClaimWandManager.enable()
        playerChangeClaimTask = task(false, 2, 4) {
            onlinePlayers.forEach { player ->
                val oldClaimName = player.teamPlayer.currentClaim
                val newClaimName = getClaimAt(player.location)?.name
                if (oldClaimName == newClaimName) return@forEach

                val oldClaim = getClaimOf(oldClaimName)
                val newClaim = getClaimOf(newClaimName)

                if (oldClaim != null) {
                    Bukkit.getPluginManager().callEvent(PlayerLeaveClaimEvent(player, oldClaim))
                }
                if (newClaim != null) {
                    Bukkit.getPluginManager().callEvent(PlayerEnterClaimEvent(player, newClaim))
                }

                player.teamPlayer.currentClaim = newClaimName
            }
        }
    }

    private fun getClaimColor(player: Player, claim: IClaim?): TextColor {
        val teamPlayer = player.teamPlayer
        return when {
            claim == null -> KColors.YELLOW
            teamPlayer.teamName == claim.name -> KColors.DARKGREEN
            claim is SpawnClaim -> KColors.LIME
            claim is KothClaim -> KColors.DARKRED
            claim is TeamClaim -> KColors.RED
            else -> KColors.YELLOW
        }
    }

    override fun disable() {
        playerChangeClaimTask?.cancel()
        playerChangeClaimTask = null
    }

    fun getClaimAt(location: Location, tolerance: Int = 0): IClaim? {
        val claims = cache.values

        // First check if there is an overlapping claim (e.g. spawn) so it doesn't return the warzone in that case
        // and then check if there is any other claim
        return claims.filterIsInstance<IOverlappingClaim>().firstOrNull { c -> c.isWithin(location, tolerance) } ?:
        claims.firstOrNull { c -> c.isWithin(location, tolerance) }
    }

    fun getTeamAt(location: Location): ITeam? {
        val claim = getClaimAt(location) ?: return null
        return Core.teamManager.teamByName(claim.name)
    }

    fun getClaimOf(name: String?) = cache[name]
    fun getClaimOf(team: ITeam?) = getClaimOf(team?.name)
    fun getClaimOf(team: PlayerTeam?) = getClaimOf(team?.name) as TeamClaim?

    fun hasOverlappingClaims(claimSelection: AbstractClaimSelection): Boolean {
        val region = claimSelection.asRegion() ?: return false
        val minLoc = region.simpleLocationPair.minSimpleLoc.withWorld(region.world)
        val minRadius = TConfig.MIN_CLAIM_RADIUS
        for (xOffset in 0..claimSelection.xDistance() step minRadius) {
            for (zOffset in 0..claimSelection.zDistance() step minRadius) {
                val location = minLoc.block.getRelative(xOffset, 0, zOffset).location
                if (getClaimAt(location, minRadius) != null) return true
            }
        }
        return false
    }

    suspend fun getNearbyClaims(centerLocation: Location, radius: Int): List<IClaim> {
        val nearbyClaims = mutableSetOf<IClaim>()
        for (xOffset in -radius..radius step 5) {
            for (zOffset in -radius..radius step 5) {
                val claim = getClaimAt(centerLocation.clone().add(xOffset, 0, zOffset)) ?: continue
                nearbyClaims.add(claim)
            }
        }
        return nearbyClaims.toList()
    }

    fun claim(claim: IClaim) {
        cache[claim.name] = claim
    }

    fun unclaim(claim: IClaim) {
        cache.remove(claim.name)
        claim.name = ""
        if (claim is TeamClaim) claim.accessibleMembers.clear()
    }

    fun rename(claim: IClaim, newName: String) {
        cache.remove(claim.name)
        cache[newName] = claim
        claim.name = newName
    }

    fun calculatePrice(region: Region, selling: Boolean): Int {
        if (region.totalBlocks() < 25) return -1
        var multiplier = 1
        var totalBlocks = region.totalBlocks()
        var finalPrice = 0.0

        while (totalBlocks > 0) {
            if (--totalBlocks % TConfig.BLOCKS_UNTIL_MULTIPLIER_INCREASES == 0) {
                multiplier++
            }
            finalPrice += TConfig.PRICE_PER_BLOCK * multiplier
        }

        if (selling) {
            finalPrice /= 100
            finalPrice *= TConfig.SELL_MULTIPLIER
        }

        return finalPrice.roundToInt()
    }

    fun calculatePrice(selection: AbstractClaimSelection, selling: Boolean): Int {
        val region = selection.asRegion() ?: return -1
        return calculatePrice(region, selling)
    }
}