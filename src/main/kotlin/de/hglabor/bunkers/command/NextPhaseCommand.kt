package de.hglabor.bunkers.command

import de.hglabor.bunkers.game.GameManager
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.runs

object NextPhaseCommand {
    fun enable() {
        command("nextphase") {
            runs {
                GameManager.startNextPhase()
            }
        }
    }
}