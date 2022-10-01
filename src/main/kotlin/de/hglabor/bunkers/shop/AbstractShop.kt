package de.hglabor.bunkers.shop

import de.hglabor.bunkers.shop.entry.IShopEntry
import de.hglabor.hcfcore.Manager
import de.hglabor.hcfcore.manager.player.teamPlayer
import de.hglabor.hcfcore.player.impl.TeamPlayer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.bukkit.spawnCleanEntity
import net.axay.kspigot.gui.InventorySlotCompound
import net.axay.kspigot.runnables.task
import net.axay.kspigot.utils.hasMark
import net.axay.kspigot.utils.mark
import org.bukkit.Location
import org.bukkit.entity.*
import org.bukkit.event.entity.EntityCombustEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerInteractEntityEvent

@Serializable
abstract class AbstractShop {
    abstract val name: String
    abstract val teamName: String
    abstract val location: Location
    abstract val villagerProfession: Villager.Profession
    abstract var villager: Entity?
    abstract val shopEntries: Map<InventorySlotCompound<*>, IShopEntry>
    @Transient
    var respawnTimer: ArmorStand? = null

    abstract fun openGUI(player: TeamPlayer)

    fun onInit() {
        listen<PlayerInteractEntityEvent> {
            if (it.rightClicked != villager) return@listen
            it.isCancelled = true
            if (villager is ZombieVillager) return@listen
            if (teamName != it.player.teamPlayer.teamName) return@listen
            openGUI(it.player.teamPlayer)
        }

        listen<EntityDamageEvent> {
            when (it.entityType) {
                EntityType.ZOMBIE_VILLAGER -> if (villager == it.entity) it.isCancelled = true
                EntityType.ARMOR_STAND -> if (respawnTimer == it.entity) it.isCancelled = true
                EntityType.VILLAGER -> if (villager == it.entity) it.damage *= 0.33
                else -> return@listen
            }
        }

        listen<EntityDamageByEntityEvent> {
            val damager = it.damager as? Player ?: return@listen
            val teamName = damager.teamPlayer.teamName ?: return@listen
            if (it.entity.hasMark(teamName)) it.isCancelled = true
        }

        listen<EntityCombustEvent> {
            if (it.entity == villager) it.isCancelled = true
        }

        listen<EntityDeathEvent> {
            if (it.entity != villager) return@listen
            it.isCancelled = true
            it.entity.health = 20.0

            villager = (villager as? Villager)?.zombify().apply {
                this?.isInvulnerable = true
                this?.isSilent = true
                this?.setAI(false)
                this?.isArmsRaised = false
                this?.mark(teamName)
            }

            respawnTimer = (location.clone().add(0.0, 0.3, 0.0).spawnCleanEntity(EntityType.ARMOR_STAND) as ArmorStand).apply {
                isInvisible = true
                isCustomNameVisible = true
                setGravity(false)
                setAI(false)
            }

            task(true, 0, 20, 16, endCallback = {
                respawnTimer?.remove()
                villager?.remove()
                villager = ShopManager.spawnVillager(this)
            }) { task ->
                respawnTimer?.customName(literalText {
                    text("Respawning: ") { color = KColors.RED }
                    text("${task.counterDownToZero}s") { color = KColors.DARKRED }
                })
            }
        }
    }
}