package de.hglabor.hcfcore.commands.teamcommand.subcommands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import de.hglabor.common.extension.next
import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.chat.ChatChannel
import de.hglabor.hcfcore.commands.teamcommand.TeamCommandCategory
import de.hglabor.hcfcore.commands.teamcommand.ITeamCommand
import de.hglabor.hcfcore.manager.player.teamPlayer
import de.hglabor.hcfcore.player.impl.TeamPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.commands.argument
import net.axay.kspigot.commands.runs
import net.axay.kspigot.commands.suggestListSuspending
import net.minecraft.commands.CommandSourceStack

object TeamChatCommand: ITeamCommand(TeamCommandCategory.GENERAL, "chat", "c") {
    override val usage: String = "/t chat (channel)"
    override val description: String = "Switch the chat channel"

    override val commandCallback: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit = {
        argument<String>("chat") {
            suggestListSuspending { ChatChannel.values().map { it.name.lowercase() } }
            runs {
                val teamPlayer = player.teamPlayer
                val team = teamPlayer.team

                // Player not on a team
                if (team == null) {
                    player.sendMsg {
                        text("You are not on a team!") { color = KColors.RED }
                    }
                    return@runs
                }

                val chatName = getArgument<String>("chat")
                var chatChannel = ChatChannel.values().firstOrNull { it.name.startsWith(chatName, true) }

                if ("team".startsWith(chatName, true)) {
                    chatChannel = ChatChannel.TEAM
                }

                if (chatChannel == null) {
                    cycleChat(teamPlayer)
                    return@runs
                }

                if (chatChannel == ChatChannel.CAPTAIN && teamPlayer.teamRole?.hasBasicPermission == false) {
                    player.sendMsg {
                        text("Only team captains can do this!") { color = KColors.RED }
                    }
                    return@runs
                }

                selectChat(teamPlayer, chatChannel)
            }
        }

        runs {
            cycleChat(player.teamPlayer)
        }
    }

    private fun cycleChat(teamPlayer: TeamPlayer) {
        val team = teamPlayer.team

        // Player not on a team
        if (team == null) {
            teamPlayer.sendMsg {
                text("You are not on a team!") { color = KColors.RED }
            }
            return
        }

        var nextChat = teamPlayer.chatChannel.next()
        if (nextChat == ChatChannel.CAPTAIN && teamPlayer.teamRole?.hasBasicPermission == false) {
            nextChat = nextChat.next()
        }
        selectChat(teamPlayer, nextChat)
    }

    private fun selectChat(teamPlayer: TeamPlayer, chatChannel: ChatChannel) {
        teamPlayer.chatChannel = chatChannel
        teamPlayer.sendMsg {
            text("You are now in ") { color = KColors.GRAY }
            text("${chatChannel.name.lowercase()} chat") { color = KColors.AQUAMARINE }
            text(".") { color = KColors.GRAY }
        }
    }
}