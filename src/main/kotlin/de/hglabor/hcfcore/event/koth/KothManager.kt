package de.hglabor.hcfcore.event.koth

import de.hglabor.auseinandersetzung.common.scoreboard.setScoreboard
import de.hglabor.common.utils.TimeConverter
import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.team.impl.KothTeam
import de.hglabor.hcfcore.listener.event.koth.PlayerCaptureKothEvent
import de.hglabor.hcfcore.manager.player.teamPlayer
import de.hglabor.hcfcore.timer.impl.KothTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.broadcast
import net.axay.kspigot.runnables.KSpigotRunnable
import net.axay.kspigot.runnables.task
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerJoinEvent
import java.util.*

object KothManager {
    val kothPrefix = literalText {
        text("[") { color = KColors.DARKGRAY }
        text("KOTH") { color = KColors.ORANGE }
        text("] ") { color = KColors.DARKGRAY }
    }
    var currentKoth: Koth? = null
    var task: KSpigotRunnable? = null

    fun startRandomKoth() {
        val koth = Core.teamManager.cache.values.filterIsInstance<KothTeam>().randomOrNull() ?: return
        startKoth(koth)
    }

    fun startKoth(koth: KothTeam) {
        currentKoth = Koth(koth, KothTimer(koth.name, 30))

        broadcast(literalText {
            component(kothPrefix)
            text("Koth ")
            text(koth.name) { color = KColors.RED }
            text(" has been started!")
        })

        registerListener()
        startTask()
    }

    private fun stopKoth() {
        val koth = currentKoth ?: return
        koth.timer.onEnd()
        stopTask()
        currentKoth = null
    }

    fun registerListener() {
        listen<PlayerJoinEvent> {
            it.player.setScoreboard {
                title = literalText("KOTH") { color = KColors.RED }
                period = 20
                content {

                    +{
                        val koth = currentKoth
                        val text = if (koth == null) "Kein Koth"
                        else TimeConverter.stringify((koth.timer.remainingTime() / 1000).toInt())
                        literalText(text)
                    }
                    +{
                        literalText(StringBuilder().apply {
                            repeat(5) {
                                append((('a')..('z')).random())
                            }
                        }.toString())
                    }
                }
            }
        }
    }

    private val contestedAnnouncement = mutableSetOf<UUID>()
    private fun startTask() {
        task = task(true, 2, 2) {
            val koth = currentKoth ?: return@task
            val king = koth.currentKing

            if (king != null) {
                if (hasBeenCaptured()) {
                    stopKoth()
                    return@task
                }

                val kingPlayer = king.player
                if (kingPlayer == null || !koth.team.claim.isInCaptureZone(kingPlayer)) {
                    koth.stopCapturing()
                } else {
                    return@task
                }
            }

            val nearbyPlayers = koth.team.claim.captureZone.centerLocation.getNearbyPlayers(10.0)
            val playersInZone = nearbyPlayers.filter {
                it.teamPlayer.teamName != null && koth.team.claim.isInCaptureZone(it)
            }

            playersInZone.singleOrNull()?.let { newKing ->
                koth.startCapturing(newKing)
                return@task
            }

            playersInZone.forEach { contesting ->
                if (contesting.uniqueId in contestedAnnouncement) return@forEach
                contesting.sendMessage(literalText {
                    component(kothPrefix)
                    text("${koth.team.name} ") { color = KColors.DARKRED}
                    text("is currently contested!")
                })
                contestedAnnouncement += contesting.uniqueId
                CoroutineScope(Dispatchers.IO).launch {
                    delay(3000)
                    contestedAnnouncement -= contesting.uniqueId
                }
            }
        }
    }

    private fun stopTask() {
        task?.cancel()
        task = null
    }

    fun hasBeenCaptured(): Boolean {
        val koth = currentKoth ?: return false
        return koth.timer.remainingTime() < 0
    }
}