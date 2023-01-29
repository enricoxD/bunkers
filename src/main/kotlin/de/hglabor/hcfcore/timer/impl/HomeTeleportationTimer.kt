package de.hglabor.hcfcore.timer.impl

import de.hglabor.common.extension.sendMsg
import de.hglabor.hcfcore.player.impl.TeamPlayer
import de.hglabor.hcfcore.timer.ITimer
import kotlinx.coroutines.*
import net.axay.kspigot.runnables.sync
import kotlin.math.max
import kotlin.time.Duration.Companion.milliseconds

class HomeTeleportationTimer(
    override val name: String,
    private val duration: Int,
    val teamPlayer: TeamPlayer
) : ITimer {
    var coroutineJob: Job? = null
    override var start: Long? = null
        set(value) {
            field = value
            if (value != null) {
                coroutineJob = CoroutineScope(Dispatchers.IO).launch {
                    delay(value.milliseconds)
                    sync {
                        onEnd()
                    }
                }
            } else {
                coroutineJob?.cancel()
            }
        }

    override fun remainingTime(): Long {
        val start = start ?: return duration * 1000L
        val end = start + (duration * 1000L)
        return max(-1, end - System.currentTimeMillis())
    }

    override fun onEnd() {
        val player = teamPlayer.player ?: return
        val teamHome = teamPlayer.team?.homeLocation ?: return
        player.teleport(teamHome)
        player.sendMsg {
            text("You have been teleported to your team's HQ.")
        }
    }
}