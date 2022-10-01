package de.hglabor.bunkers.listener.koth

import de.hglabor.bunkers.game.GameManager
import de.hglabor.bunkers.game.phase.phases.EndPhase
import de.hglabor.bunkers.game.phase.phases.IngamePhase
import de.hglabor.bunkers.teams.BunkersTeam
import de.hglabor.hcfcore.listener.event.koth.PlayerCaptureKothEvent
import de.hglabor.hcfcore.manager.player.teamPlayer
import net.axay.kspigot.event.listen

object BunkersCaptureKothEvent {

    fun register() {
        listen<PlayerCaptureKothEvent> {
            if (GameManager.currentPhase != IngamePhase) return@listen

            val team = it.player.teamPlayer.team as? BunkersTeam ?: return@listen
            EndPhase.winner = team
            GameManager.startNextPhase()
        }
    }
}