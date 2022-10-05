package de.hglabor.bunkers.mechanics

import net.axay.kspigot.event.listen
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.player.PlayerAdvancementDoneEvent

object Protection {
    fun enable() {
        listen<EntitySpawnEvent> {
            if (it.entity.entitySpawnReason == CreatureSpawnEvent.SpawnReason.RAID) {
                it.isCancelled = true
            }
        }

        listen<PlayerAdvancementDoneEvent> {
            it.message(null)
        }
    }
}