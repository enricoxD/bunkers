package de.hglabor.hcfcore.commands.hcfcorecommand

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import de.hglabor.hcfcore.commands.hcfcorecommand.subcommands.HCFCoreClaimCommand
import de.hglabor.hcfcore.commands.hcfcorecommand.subcommands.HCFCoreForceunclaimCommand
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.requiresPermission
import net.minecraft.commands.CommandSourceStack

object AdminCommand {
    fun register() {
        command("hcfcore") {
            requiresPermission("hglabor.admin")
            then(HCFCoreClaimCommand.cmd)
            then(HCFCoreForceunclaimCommand.cmd)
        }
    }
}

fun hcfCoreCommand(name: String, builder: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit) = command(name, false, builder)