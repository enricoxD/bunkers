package de.hglabor.hcfcore

import de.hglabor.common.utils.BrigadierUtils
import de.hglabor.hcfcore.chat.ChatHandler
import de.hglabor.hcfcore.configuration.TConfig
import de.hglabor.hcfcore.database.mongodb.MongoDatabase
import de.hglabor.hcfcore.database.mongodb.MongoManager
import de.hglabor.hcfcore.manager.claim.IClaimManager
import de.hglabor.hcfcore.manager.claim.impl.FlatFileClaimManager
import de.hglabor.hcfcore.manager.claim.impl.MemoryClaimManager
import de.hglabor.hcfcore.manager.claim.impl.MongoClaimManager
import de.hglabor.hcfcore.manager.player.IPlayerManager
import de.hglabor.hcfcore.manager.player.impl.FlatFilePlayerManager
import de.hglabor.hcfcore.manager.player.impl.MemoryPlayerManager
import de.hglabor.hcfcore.manager.player.impl.MongoPlayerManager
import de.hglabor.hcfcore.manager.team.ITeamManager
import de.hglabor.hcfcore.manager.team.impl.FlatFileTeamManager
import de.hglabor.hcfcore.manager.team.impl.MemoryTeamManager
import de.hglabor.hcfcore.manager.team.impl.MongoTeamManager
import de.hglabor.hcfcore.mechanics.fixes.PearlGlitchListener
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.Component
import org.bukkit.plugin.java.JavaPlugin

class HCFCore(rootPlugin: JavaPlugin) {
    companion object {
        lateinit var core: HCFCore; private set
        lateinit var plugin: JavaPlugin; private set
        lateinit var prefix: Component; private set
    }
    lateinit var playerManager: IPlayerManager
    lateinit var teamManager: ITeamManager
    lateinit var claimManager: IClaimManager

    private val cachingMethod by lazy { TConfig.CACHING_METHOD.lowercase() }

    init {
        plugin = rootPlugin
    }

    fun enable(
        _prefix: Component,
        playerManager: IPlayerManager = getDefaultPlayerManager(),
        teamManager: ITeamManager = getDefaultTeamManager(),
        claimManager: IClaimManager = getDefaultClaimManager(),
    ) {
        core = this
        prefix = _prefix

        this.playerManager = playerManager
        this.teamManager = teamManager
        this.claimManager = claimManager

        //BrigadierUtils.unregisterCommand("team")
        enableManagers()
        ChatHandler.register()

        if (TConfig.FIX_PEARL_GLITCHING) {
            PearlGlitchListener.enable()
        }
    }

    fun disable() {
        disableManagers()
    }
    
   fun getDefaultPlayerManager(): IPlayerManager {
        return when (cachingMethod) {
            "flatfile" -> FlatFilePlayerManager()
            "mongodb" -> MongoPlayerManager()
            else -> MemoryPlayerManager()
        }
    }

    fun getDefaultTeamManager(): ITeamManager {
        return when (cachingMethod) {
            "flatfile" -> FlatFileTeamManager()
            "mongodb" -> MongoTeamManager()
            else -> MemoryTeamManager()
        }
    }

    fun getDefaultClaimManager(): IClaimManager {
        return when (cachingMethod) {
            "flatfile" -> FlatFileClaimManager()
            "mongodb" -> MongoClaimManager()
            else -> MemoryClaimManager()
        }
    }

    private fun enableManagers() {
        val managers = listOf(claimManager, teamManager, playerManager)
        if (managers.any { it is MongoDatabase<*> }) {
            runBlocking {
                MongoManager.connectJob.join()
            }
        }
        teamManager.enable()
        claimManager.enable()
        playerManager.enable()
    }

    private fun disableManagers() {
        teamManager.disable()
        playerManager.disable()
        claimManager.disable()
    }
}

val Prefix by lazy { HCFCore.prefix }
val Core by lazy { HCFCore.core }
val Manager by lazy { HCFCore.plugin }