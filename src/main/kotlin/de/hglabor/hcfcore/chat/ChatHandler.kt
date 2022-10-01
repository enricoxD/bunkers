package de.hglabor.hcfcore.chat

import de.hglabor.hcfcore.manager.player.teamPlayer
import net.axay.kspigot.event.listen
import org.bukkit.event.player.AsyncPlayerChatEvent

object ChatHandler {

    fun register() {
        listen<AsyncPlayerChatEvent> {
            val player = it.player
            val teamPlayer = player.teamPlayer
            var message = it.message
            var removeFirst = false

            it.isCancelled = true

            val channel =
                if (teamPlayer.teamName == null) ChatChannel.PUBLIC
                else
                    when (message.first()) {
                        '!' -> {
                            removeFirst = true
                            ChatChannel.PUBLIC
                        }
                        '@' -> {
                            removeFirst = true
                            ChatChannel.TEAM
                        }
                        //'#' -> ChatChannel.ALLY
                        else -> teamPlayer.chatChannel
                    }

            if (removeFirst) message = message.drop(1)
            channel.sendMessage(player, message)
        }
    }
}