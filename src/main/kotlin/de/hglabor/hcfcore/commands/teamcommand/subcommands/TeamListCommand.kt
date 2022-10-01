package de.hglabor.hcfcore.commands.teamcommand.subcommands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.commands.teamcommand.TeamCommandCategory
import de.hglabor.hcfcore.commands.teamcommand.ITeamCommand
import de.hglabor.hcfcore.team.impl.PlayerTeam
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.commands.argument
import net.axay.kspigot.commands.runs
import net.minecraft.commands.CommandSourceStack
import org.bukkit.entity.Player

object TeamListCommand : ITeamCommand(TeamCommandCategory.INFORMATION, "list") {
    override val usage: String = "/t list (page#)"
    override val description: String = "List existing teams"

    override val commandCallback: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit = {
        argument<Int>("page") {
            runs {
                listPage(player, getArgument<Int>("page") - 1)
            }
        }

        runs {
            listPage(player, 0)
        }
    }

    fun listPage(player: Player, page: Int) {
        val allTeams = Core.teamManager.cache.values
            .filterIsInstance<PlayerTeam>()
            .filter { it.players.isNotEmpty() }
        val pages = allTeams.windowed(10, 10, true)

        if (allTeams.isEmpty()) {
            player.sendMsg {
                text("Currently there are ") { color = KColors.GRAY }
                text("no pages") { color = KColors.RED }
                text(".") { color = KColors.GRAY }
            }
            return
        }

        if (page > pages.size) {
            player.sendMsg {
                text("Usage: $usage") { color = KColors.RED }
            }
            player.sendMsg {
                text("Currently there are ") { color = KColors.GRAY }
                text("${pages.size} Pages") { color = KColors.FLORALWHITE }
                text(".") { color = KColors.GRAY }
            }
            return
        }

        val teams = pages[page]

        val strike = literalText { text("-------------------------") { color = KColors.DARKGRAY; strikethrough = true } }
        player.sendMessage(strike)
        player.sendMessage(literalText {
            text("Team List ") { color = KColors.BLUE }
            text("(Page ${page + 1}/${pages.size})") { color = KColors.FLORALWHITE }
        })
        teams.forEachIndexed { index, fac ->
            player.sendMessage(literalText {
                text("${index + 1}. ")
                text("${fac.name} ") { color = KColors.AQUAMARINE }
                text("(${fac.players.size}/${fac.members.size})") { color = KColors.FLORALWHITE }
            })
        }
        player.sendMessage(strike)
        player.sendMessage(literalText {
            text("You are currently on ") { color = KColors.GRAY }
            text("Page ${page + 1}/${pages.size}") { color = KColors.FLORALWHITE }
            text(".") { color = KColors.GRAY }
        })
        player.sendMessage(literalText {
            text("To view other pages, use ") { color = KColors.GRAY }
            text(usage) { color = KColors.FLORALWHITE }
        })
        player.sendMessage(strike)
    }
}