package de.hglabor.hcfcore.commands.teamcommand

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import de.hglabor.hcfcore.commands.teamcommand.subcommands.*
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.runs
import net.minecraft.commands.CommandSourceStack

object TeamCommand {
    lateinit var subCommands: List<ITeamCommand>
    private val categorizedCommands by lazy {
        subCommands.sortedBy { it.category.index }.groupBy { it.category }.toSortedMap()
    }

    fun register(
        vararg teamCommands: ITeamCommand = arrayOf(
            TeamChatCommand,
            TeamClaimCommand,
            TeamCreateCommand,
            TeamDemoteCommand,
            TeamDepositCommand,
            TeamDisbandCommand,
            TeamInfoCommand,
            TeamInviteCommand,
            TeamJoinCommand,
            TeamKickCommand,
            TeamLeaderCommand,
            TeamLeaveCommand,
            TeamListCommand,
            TeamMapCommand,
            TeamPromoteCommand,
            TeamRenameCommand,
            TeamSetHomeCommand,
            TeamUnclaimCommand,
            TeamUninviteCommand,
            TeamWhoCommand,
            TeamWithdrawCommand
        )
    ) {
        subCommands = teamCommands.toList()

        listOf("team", "f", "t", "team").forEach { alias ->
            command(alias) {
                subCommands.forEach { sc ->
                    sc.aliases.forEach { alias ->
                        then(command(alias, false, sc.commandCallback))
                    }
                }

                runs {
                    player.sendMessage(literalText {
                        text("               |") { color = KColors.DARKGRAY; strikethrough = true }
                        text(" Team Help ") { color = KColors.AQUAMARINE }
                        text("|               ") { color = KColors.DARKGRAY; strikethrough = true }
                    })

                    categorizedCommands.forEach { (category, subCommands) ->
                        player.sendMessage(literalText {
                            text("${category.displayName}:") { color = KColors.DARKAQUA }
                        })
                        subCommands.forEach { sc ->
                            player.sendMessage(
                                literalText {
                                    text(" ${sc.usage} ") { color = KColors.FLORALWHITE }
                                    text("- ") { color = KColors.DARKGRAY }
                                    text(sc.description) { color = KColors.GRAY }
                                }
                            )
                        }
                        if (category != TeamCommandCategory.LEADER) {
                            player.sendMessage("")
                        }
                    }

                    player.sendMessage(
                        literalText("                                           ") {
                            color = KColors.DARKGRAY
                            strikethrough = true
                        }
                    )
                }
            }
        }
    }
}

abstract class ITeamCommand(val category: TeamCommandCategory, vararg val aliases: String) {
    abstract val usage: String
    abstract val description: String
    abstract val commandCallback: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit
}

enum class TeamCommandCategory(val displayName: String, val index: Int) {
    GENERAL("General", 0),
    INFORMATION("Information", 1),
    CAPTAIN("Captain", 2),
    LEADER("Leader", 3)
}
