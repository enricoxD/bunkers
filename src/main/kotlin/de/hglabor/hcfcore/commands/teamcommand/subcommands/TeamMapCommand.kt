package de.hglabor.hcfcore.commands.teamcommand.subcommands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import de.hglabor.hcfcore.commands.teamcommand.TeamCommandCategory
import de.hglabor.hcfcore.commands.teamcommand.ITeamCommand
import de.hglabor.hcfcore.visualization.VisualizationHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.axay.kspigot.commands.runs
import net.minecraft.commands.CommandSourceStack

object TeamMapCommand: ITeamCommand(TeamCommandCategory.INFORMATION, "map") {
    override val usage: String = "/t map"
    override val description: String = "Show nearby claims"

    override val commandCallback: LiteralArgumentBuilder<CommandSourceStack>.() -> Unit = {
        runs {
            player.sendMessage("soon..")
            return@runs
            // TODO
            CoroutineScope(Dispatchers.IO).launch {
                VisualizationHandler.showNearbyClaims(player)
            }
        }
    }
}