package de.hglabor.bunkers.shop

import de.hglabor.bunkers.shop.shops.BuildShop
import de.hglabor.bunkers.shop.shops.CombatShop
import de.hglabor.bunkers.shop.shops.EnchantmentShop
import de.hglabor.bunkers.shop.shops.SellShop
import de.hglabor.hcfcore.database.flatfile.FlatFileDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.utils.mark
import org.bukkit.entity.EntityType
import org.bukkit.entity.Villager
import org.bukkit.event.entity.CreatureSpawnEvent

object ShopManager : FlatFileDatabase("shops") {

    val shops: MutableList<AbstractShop> = mutableListOf()

    override val stringFormat = Json {
        serializersModule = SerializersModule {
            polymorphic(AbstractShop::class, BuildShop::class, BuildShop.serializer())
            polymorphic(AbstractShop::class, CombatShop::class, CombatShop.serializer())
            polymorphic(AbstractShop::class, EnchantmentShop::class, EnchantmentShop.serializer())
            polymorphic(AbstractShop::class, SellShop::class, SellShop.serializer())
        }
        encodeDefaults = true
    }

    fun enable() {
        CoroutineScope(Dispatchers.IO).launch {
            fetchDatabase()
        }
    }

    fun disable() {
        CoroutineScope(Dispatchers.IO).launch {
            saveDatabase()
        }
    }

    fun registerShop(abstractShop: AbstractShop) {
        shops.add(abstractShop)
    }

    fun spawnAllVillagers() {
        shops.forEach { shop ->
            if (shop.villager == null)
                shop.villager = spawnVillager(shop)
            shop.villager?.getNearbyEntities(0.5, 0.5, 0.5)?.forEach { it.remove() }
        }
    }

    fun spawnVillager(abstractShop: AbstractShop): Villager {
        val location = abstractShop.location
        val villager = location.world.spawnEntity(location, EntityType.VILLAGER, CreatureSpawnEvent.SpawnReason.CUSTOM) as Villager

        return villager.apply {
            setAdult()
            ageLock = true
            profession = abstractShop.villagerProfession
            removeWhenFarAway = false
            setAI(false)
            isInvulnerable = false
            isAware = false
            isCollidable = false
            isSilent = true
            customName(literalText(abstractShop.name) { color = KColors.DARKAQUA })
            isCustomNameVisible = true
            health = 20.0
            mark(abstractShop.teamName)
        }
    }

    override suspend fun fetchDatabase() {
        val jsonString = file.readText()
        if (jsonString.isBlank()) return

        shops.addAll(stringFormat.decodeFromString<List<AbstractShop>>(jsonString))
    }

    override suspend fun saveDatabase() {
        file.writeText(stringFormat.encodeToString(shops))
    }
}
