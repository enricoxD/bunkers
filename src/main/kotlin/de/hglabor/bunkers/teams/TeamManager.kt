package de.hglabor.bunkers.teams

import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.configuration.ConfigDelegate
import de.hglabor.hcfcore.manager.team.impl.FlatFileTeamManager
import de.hglabor.hcfcore.team.ITeam
import de.hglabor.hcfcore.team.impl.KothTeam
import de.hglabor.hcfcore.team.impl.PlayerTeam
import de.hglabor.hcfcore.team.impl.SpawnTeam
import de.hglabor.hcfcore.team.impl.WarzoneTeam
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import net.axay.kspigot.chat.KColors
import org.bukkit.Material

class TeamManager: FlatFileTeamManager() {
    companion object {
        val MAX_PLAYERS_PER_FACTION by ConfigDelegate(4, "bunkers")
        val blue by lazy { Core.teamManager.teamByName("Blue") as? BunkersTeam? ?: error("Team Blue doesn't exist?") }
        val yellow by lazy { Core.teamManager.teamByName("Yellow") as? BunkersTeam? ?: error("Team Yellow doesn't exist?") }
        val green by lazy { Core.teamManager.teamByName("Green") as? BunkersTeam? ?: error("Team Green doesn't exist?") }
        val red by lazy { Core.teamManager.teamByName("Red") as? BunkersTeam? ?: error("Team Red doesn't exist?") }
    }

    override val stringFormat: StringFormat = Json {
        serializersModule = SerializersModule {
            polymorphic(ITeam::class, PlayerTeam::class, PlayerTeam.serializer())
            polymorphic(ITeam::class, SpawnTeam::class, SpawnTeam.serializer())
            polymorphic(ITeam::class, KothTeam::class, KothTeam.serializer())
            polymorphic(ITeam::class, WarzoneTeam::class, WarzoneTeam.serializer())
            polymorphic(ITeam::class, BunkersTeam::class, BunkersTeam.serializer())
        }
        encodeDefaults = true
    }

    override suspend fun fetchDatabase() {
        super.fetchDatabase()
        
        if (cache.isEmpty()) {
            registerTeam(BunkersTeam("Blue", KColors.DEEPSKYBLUE, Material.LIGHT_BLUE_WOOL))
            registerTeam(BunkersTeam("Yellow", KColors.YELLOW, Material.YELLOW_WOOL))
            registerTeam(BunkersTeam("Green", KColors.LIME, Material.LIME_WOOL))
            registerTeam(BunkersTeam("Red", KColors.RED, Material.RED_WOOL))
        }
    }
}