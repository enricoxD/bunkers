package de.hglabor.bunkers.mechanics

import net.axay.kspigot.event.listen
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntitySpawnEvent

object Protection {
    fun enable() {
        listen<EntitySpawnEvent> {
            if (it.entity.entitySpawnReason == CreatureSpawnEvent.SpawnReason.RAID) {
                it.isCancelled = true
            }
        }
    }
}