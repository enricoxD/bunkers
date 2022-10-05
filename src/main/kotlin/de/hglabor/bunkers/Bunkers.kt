package de.hglabor.bunkers

import com.mojang.authlib.GameProfile
import de.hglabor.bunkers.command.BunkersCommand
import de.hglabor.bunkers.command.NextPhaseCommand
import de.hglabor.bunkers.game.GameManager
import de.hglabor.bunkers.game.pvpstyle.impl.ClassicStyle
import de.hglabor.bunkers.game.pvpstyle.impl.PotStyle
import de.hglabor.bunkers.game.pvpstyle.impl.SoupStyle
import de.hglabor.bunkers.listener.claims.PlayerClaimListeners
import de.hglabor.bunkers.mechanics.BlockBreak
import de.hglabor.bunkers.mechanics.PlayerRespawn
import de.hglabor.bunkers.mechanics.Protection
import de.hglabor.bunkers.shop.ShopManager
import de.hglabor.bunkers.teams.TeamManager
import de.hglabor.hcfcore.HCFCore
import de.hglabor.hcfcore.commands.BalanceCommand
import de.hglabor.hcfcore.commands.KothCommand
import de.hglabor.hcfcore.commands.PayCommand
import de.hglabor.hcfcore.commands.hcfcorecommand.AdminCommand
import de.hglabor.hcfcore.commands.teamcommand.TeamCommand
import de.hglabor.hcfcore.commands.teamcommand.subcommands.*
import de.hglabor.hcfcore.manager.claim.impl.FlatFileClaimManager
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.main.KSpigot
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import org.bukkit.World
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld


class Bunkers : KSpigot() {
    private val hcfCore = HCFCore(this)
    private val prefix = literalText {
        text(" | ") { color = KColors.DARKGRAY }
        text("Bunkers ") { color = KColors.MEDIUMAQUAMARINE }
        text("» ") { color = KColors.DARKGRAY }
    }

    override fun startup() {
        hcfCore.enable(prefix, teamManager = TeamManager(), claimManager = FlatFileClaimManager())
        ShopManager.enable()
        Protection.enable()
        registerCommands()
        registerPvPStyles()
        GameManager.enable()
        PlayerRespawn.enable()
        PlayerClaimListeners.enable()
        BlockBreak.enable()
        GameManager.pvpStyleManager.style?.enable()
    }

    override fun shutdown() {
        hcfCore.disable()
        ShopManager.disable()
    }

    private fun registerPvPStyles() {
        GameManager.pvpStyleManager.register(PotStyle)
        GameManager.pvpStyleManager.register(SoupStyle)
        GameManager.pvpStyleManager.register(ClassicStyle)
    }

    private fun registerCommands() {
        NextPhaseCommand.enable()
        AdminCommand.register()
        BalanceCommand.register()
        PayCommand.register()
        KothCommand.register()
        TeamCommand.register(
            TeamChatCommand,
            TeamHomeCommand,
            TeamInfoCommand,
            TeamListCommand,
            TeamWhoCommand,
        )
        BunkersCommand.register()
    }
}

