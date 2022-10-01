package de.hglabor.hcfcore.team.claim.selection

import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.team.claim.claimwand.ClaimWandManager
import de.hglabor.hcfcore.visualization.VisualizationHandler
import net.axay.kspigot.chat.KColors
import org.bukkit.Bukkit
import java.util.*
import kotlin.reflect.full.createInstance

object ClaimSelectionManager {
    val claimSelections: MutableMap<UUID, AbstractClaimSelection> = mutableMapOf()

    fun getSelection(uuid: UUID) = claimSelections[uuid]
    inline fun <reified T: AbstractClaimSelection> getOrCreateSelection(uuid: UUID): T {
        return claimSelections[uuid] as? T ?: T::class.createInstance().also { claimSelections[uuid] = it }
    }

    fun clearSelection(uuid: UUID, sendMessage: Boolean = true) {
        val selection = claimSelections[uuid] ?: return
        selection.loc1 = null
        selection.loc2 = null
        Bukkit.getPlayer(uuid)?.let { p ->
            if (sendMessage) {
                p.sendMsg {
                    text("You cleared your claim selection!") { color = KColors.RED }
                }
            }
            p.inventory.removeItemAnySlot(ClaimWandManager.claimWandItem)
        }
        claimSelections.remove(uuid)
        VisualizationHandler.hideVisuals(uuid)
    }
}