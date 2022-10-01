package de.hglabor.common.utils

import com.mojang.brigadier.tree.RootCommandNode
import de.hglabor.hcfcore.Manager
import net.minecraft.server.dedicated.DedicatedServer
import org.bukkit.craftbukkit.v1_19_R1.CraftServer

object BrigadierUtils {
    val dedicatedServer: DedicatedServer
    private val rootCommandNode: RootCommandNode<*>

    init {
        val consoleField = CraftServer::class.java.declaredFields.first { it.name == "console" }
        consoleField.isAccessible = true
        dedicatedServer = consoleField.get(Manager.server) as DedicatedServer
        consoleField.isAccessible = false
        rootCommandNode = dedicatedServer.commands.dispatcher.root
    }

    fun unregisterCommands(vararg commandNames: String) {
        rootCommandNode.children.removeIf { it.name in commandNames }
    }

    fun unregisterCommand(commandName: String) {
        rootCommandNode.children.removeIf { it.name.equals(commandName, true) }
    }
}