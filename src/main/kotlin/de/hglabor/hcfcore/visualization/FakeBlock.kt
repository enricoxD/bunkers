package de.hglabor.hcfcore.visualization

import net.axay.kspigot.runnables.async
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player

class FakeBlock(val location: Location, val material: Material) {

    fun show(player: Player) {
        async {
            player.sendBlockChange(location, material.createBlockData())
        }
    }

    fun hide(player: Player) {
        async {
            player.sendBlockChange(location, location.block.type.createBlockData())
        }
    }
}