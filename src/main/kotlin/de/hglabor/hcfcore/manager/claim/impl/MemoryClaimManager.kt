package de.hglabor.hcfcore.manager.claim.impl

import de.hglabor.hcfcore.team.claim.IClaim
import de.hglabor.hcfcore.manager.claim.IClaimManager
import de.hglabor.hcfcore.team.claim.selection.ClaimSelectionManager
import net.axay.kspigot.runnables.KSpigotRunnable

open class MemoryClaimManager: IClaimManager {
    override var cache: MutableMap<String, IClaim> = mutableMapOf()
    override val claimSelectionManager = ClaimSelectionManager
    override var playerChangeClaimTask: KSpigotRunnable? = null
}