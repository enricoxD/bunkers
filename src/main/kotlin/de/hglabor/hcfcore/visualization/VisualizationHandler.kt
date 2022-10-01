package de.hglabor.hcfcore.visualization

import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.configuration.TConfig
import de.hglabor.hcfcore.team.claim.Region
import de.hglabor.hcfcore.team.claim.selection.AbstractClaimSelection
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.runnables.KSpigotRunnable
import net.axay.kspigot.runnables.async
import net.axay.kspigot.runnables.task
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*

object VisualizationHandler {
    val visuals: MutableMap<UUID, MutableSet<FakeBlock>> = mutableMapOf()
    private var task: KSpigotRunnable? = null

    private fun runTask() {
        task = task(false, 2, 2) {

            visuals.toMap().forEach { (uuid, blocks) ->
                val player = Bukkit.getPlayer(uuid)
                if (player == null) {
                    hideVisuals(uuid)
                    return@forEach
                }
                blocks.forEach { vb ->
                    vb.show(player)
                }
            }
        }
    }

    private fun stopTask() {
        task?.cancel()
        task = null
    }

    private fun showFakeBlock(player: Player, location: Location, material: Material) {
        val fb = FakeBlock(location, material)
        fb.show(player)
        visuals.computeIfAbsent(player.uniqueId) { mutableSetOf() }.add(fb)
        if (task == null) {
            runTask()
        }
    }

    fun showRegionCorners(player: Player, material: Material, region: Region) {
        val playerY = player.location.blockY
        val loc1 = region.loc1
        val loc2 = region.loc2
        val world = region.world

        async {
            val yLocations = (-32..32).map { offset -> playerY + offset }
            yLocations.forEachIndexed { index, y ->
                if (y > 320 || y < -64) return@forEachIndexed
                val mat = if (index % 4 == 0) material else Material.GLASS

                loc1.let { l ->
                    val loc = Location(world, l.x, y.toDouble(), l.z)
                    if (loc.block.type == Material.AIR) {
                        showFakeBlock(player, loc, mat)
                    }
                }

                loc2.let { l ->
                    val loc = Location(world, l.x, y.toDouble(), l.z)
                    if (loc.block.type == Material.AIR) {
                        showFakeBlock(player, loc, mat)
                    }
                }
            }
        }
    }

    fun showSelectionCorners(player: Player, material: Material, claimSelection: AbstractClaimSelection) {
        hideVisuals(player)
        val playerY = player.location.blockY
        val loc1 = claimSelection.loc1
        val loc2 = claimSelection.loc2
        val world = loc1?.world ?: loc2?.world ?: return

        async {
            val yLocations = (-32..32).map { offset -> playerY + offset }
            yLocations.forEach { y ->
                if (y > 320 || y < -64) return@forEach

                loc1?.let { l ->
                    val loc = Location(world, l.x, y.toDouble(), l.z)
                    if (loc.block.type == Material.AIR) {
                        showFakeBlock(player, loc, material)
                    }
                }

                loc2?.let { l ->
                    val loc = Location(world, l.x, y.toDouble(), l.z)
                    if (loc.block.type == Material.AIR) {
                        showFakeBlock(player, loc, material)
                    }
                }
            }
        }
    }

    val claimMaterials = Material.values().filter { it.name.endsWith("_CONCRETE", true) }

    suspend fun showNearbyClaims(player: Player) {
        val nearbyClaims = Core.claimManager.getNearbyClaims(player.location, TConfig.F_MAP_RADIUS)

        if (nearbyClaims.isEmpty()) {
            player.sendMsg {
                text("There are no nearby claims.") { color = KColors.RED }
            }
            return
        }

        nearbyClaims.forEachIndexed { index, claim ->
            val name = claim.name
            val region = claim.region
            val material = claimMaterials[index]
            player.sendMsg {
                text("Land ") { color = KColors.GRAY }
                text("$name ") { color = KColors.AQUAMARINE }
                text("has been visualized with ") { color = KColors.GRAY }
                text(material.name.lowercase().replaceFirstChar { it.uppercaseChar() }) { color = KColors.BLUE }
                text(".") { color = KColors.GRAY }
            }
            showRegionCorners(player, material, region)
        }
    }

    fun hideVisuals(uuid: UUID) {
        hideVisuals(Bukkit.getPlayer(uuid) ?: return)
    }

    fun hideVisuals(player: Player) {
        async {
            if (player.uniqueId !in visuals) return@async
            val v = visuals[player.uniqueId]?.toList()
            visuals.remove(player.uniqueId)
            v?.toList()?.forEach { vb ->
                vb.hide(player)
            }
        }

        if (visuals.isEmpty()) {
            stopTask()
        }
    }
}