package de.hglabor.common.extension

import de.hglabor.hcfcore.Prefix
import de.hglabor.hcfcore.player.impl.TeamPlayer
import net.axay.kspigot.chat.LiteralTextBuilder
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.runnables.async
import net.kyori.adventure.text.Component
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer
import org.bukkit.entity.Player

fun Player.sendMsg(message: Component) = sendMessage(Prefix.append(message))
fun Player.sendMsg(builder: LiteralTextBuilder.() -> Unit) =
    sendMsg(LiteralTextBuilder("").apply(builder).build())

fun TeamPlayer.sendMsg(message: Component) = player?.sendMsg(message)
fun TeamPlayer.sendMsg(builder: LiteralTextBuilder.() -> Unit) = player?.sendMsg(builder)

val Player.serverPlayer get() = (this as CraftPlayer).handle
val Player.connection get() = serverPlayer.connection

fun broadcast(builder: LiteralTextBuilder.() -> Unit) {
    async {
        onlinePlayers.forEach { player ->
            player.sendMsg(builder)
        }
    }
}