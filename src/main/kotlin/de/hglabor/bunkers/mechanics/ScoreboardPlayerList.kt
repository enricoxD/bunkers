package de.hglabor.bunkers.mechanics

import de.hglabor.auseinandersetzung.common.scoreboard.setScoreboard
import de.hglabor.bunkers.game.GameManager
import de.hglabor.bunkers.game.phase.phases.LobbyPhase
import de.hglabor.bunkers.teams.BunkersTeam
import de.hglabor.bunkers.teams.TeamManager
import de.hglabor.common.playerlist.SkinTexture
import de.hglabor.common.playerlist.body.PlayerListBody
import de.hglabor.common.playerlist.builder.PlayerListBodyBuilder
import de.hglabor.common.playerlist.builder.PlayerListColumnBuilder
import de.hglabor.common.text.mcText
import de.hglabor.hcfcore.event.koth.KothManager
import de.hglabor.hcfcore.manager.player.teamPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

object ScoreboardPlayerList {
    fun enable() {
        listen<PlayerJoinEvent> {
            setPlayerList(it.player)
            setScoreboard(it.player)
        }
    }

    private fun setScoreboard(player: Player) {
        player.setScoreboard {
            title = literalText {
                text("HGLabor") { color = KColors.DEEPSKYBLUE; bold = true }
                text(" | ") { color = KColors.DARKGRAY }
                text("Bunkers") { color = KColors.WHITE }
            }

            content {
                +{
                    if (GameManager.currentPhase == LobbyPhase) {
                        literalText {
                            text("Start: ") { color = KColors.GRAY; bold = true }
                            GameManager.currentPhase.remainingTime.seconds.toComponents { min, sec, _ ->
                                text("${min}:${sec}") { color = KColors.FLORALWHITE }
                            }
                        }
                    } else {
                        literalText {
                            text("Game Time: ") { color = KColors.GRAY }
                            GameManager.elapsedTime.seconds.toComponents { min, sec, _ ->
                                text("${min}:${sec}") { color = KColors.FLORALWHITE }
                            }
                        }
                    }
                }

                + {
                    literalText {
                        val koth = KothManager.currentKoth
                        text("${koth?.team?.name ?: "KOTH"}: ") { color = KColors.ORANGE; bold = true }
                        if (koth == null) {
                            text("Hasn't started") { color = KColors.WHITE }
                        } else {
                            koth.timer.remainingTime().milliseconds.toComponents { min, sec, _ ->
                                text("${min}:${sec}") { color = KColors.FLORALWHITE }
                            }
                        }
                    }
                }

                + {
                    literalText {
                        text("Balance: ") { color = KColors.GREEN; bold = true }
                        text("$${player.teamPlayer.balance}") { color = KColors.FLORALWHITE }
                    }
                }
            }
        }
    }

    private fun setPlayerList(player: Player) {
        player.setPlayerList {
            +column(0) {
                +entry(2) {
                    name(mcText("Team Info") { color = KColors.AQUAMARINE.value(); bold = true })
                }

                +entry(3) {
                    name {
                        val team = player.teamPlayer.team
                        if (team == null)
                            text("No team") { color = KColors.RED.value() }
                        else {
                            text("Online: ") { color = KColors.GRAY.value() }
                            text("${team.players.size}/${TeamManager.MAX_PLAYERS_PER_FACTION}") {
                                color = KColors.WHITE.value()
                            }
                        }
                    }
                }

                +entry(4) {
                    name {
                        val team = player.teamPlayer.team ?: return@name
                        text("DTR: ") { color = KColors.GRAY.value() }
                        text("${team.dtr}") { color = KColors.WHITE.value() }
                    }
                }

                +entry(5) {
                    name {
                        val team = player.teamPlayer.team ?: return@name
                        val homeLocation = team.homeLocation ?: return@name
                        text("Home: ") { color = KColors.GRAY.value() }
                        text("${homeLocation.blockX}, ${homeLocation.blockZ}") { color = KColors.WHITE.value() }
                    }
                }

                +entry(2 + TeamManager.MAX_PLAYERS_PER_FACTION + 4) {
                    name(mcText("Game Info") { color = KColors.AQUAMARINE.value(); bold = true })
                }

                +entry(3 + TeamManager.MAX_PLAYERS_PER_FACTION + 4) {
                    name {
                        val style = GameManager.pvpStyleManager.style
                        text("Style: ") { color = KColors.GRAY.value() }
                        text(style?.name ?: "Voting") { color = KColors.WHITE.value() }
                    }
                }
            }

            +column(1) {
                addTeamEntries(2, TeamManager.yellow, SkinTexture.YELLOW)

                addTeamEntries(2 + TeamManager.MAX_PLAYERS_PER_FACTION + 4, TeamManager.blue, SkinTexture.BLUE)
            }

            +column(2) {
                addTeamEntries(2, TeamManager.red, SkinTexture.RED)

                addTeamEntries(2 + TeamManager.MAX_PLAYERS_PER_FACTION + 4, TeamManager.green, SkinTexture.GREEN)
            }

            +column(3) {
                addSpectators()
            }
        }
    }

    private fun PlayerListColumnBuilder.addTeamEntries(y: Int, team: BunkersTeam, skin: SkinTexture) {
        +entry(y) {
            name {
                text("Team ${team.name}") { color = team.teamColor.value();bold = true }
                text(" | ") { color = KColors.DARKGRAY.value() }
                text("${team.dtr}") { color = KColors.FLORALWHITE.value() }
            }
            skin(skin)
        }

        for (i in 0..TeamManager.MAX_PLAYERS_PER_FACTION) {
            +entry(y + 1 + i) {
                name {
                    val member = team.teamPlayers.getOrNull(i)
                    val nameColor = when {
                        member?.uuid in PlayerRespawn.eliminatedPlayers -> KColors.DARKGRAY
                        member?.player == null ||
                                member.player?.isOnline == false -> KColors.GRAY

                        else -> team.teamColor
                    }

                    text(member?.name ?: "") {
                        if (member == null) return@text
                        color = nameColor.value()

                        strikethrough = (when (member.uuid) {
                            in PlayerRespawn.eliminatedPlayers,
                            in PlayerRespawn.respawningPlayers -> true

                            else -> false
                        })
                    }
                }

                skin {
                    val member = team.teamPlayers.getOrNull(i)
                    val bukkitPlayer = member?.player

                    when {
                        bukkitPlayer != null -> SkinTexture.PlayerSkinTexture(bukkitPlayer)
                        else -> SkinTexture.GRAY
                    }
                }
            }
        }
    }

    private fun PlayerListColumnBuilder.addSpectators() {
        +entry(2) {
            name {
                text(
                    if (GameManager.currentPhase == LobbyPhase) "No Team"
                    else "Spectators"
                ) {
                    color = KColors.DIMGRAY.value()
                    bold = true
                }
            }
        }

        for (i in 0 until 15) {
            +entry(3 + i) {
                name {
                    val spec = onlinePlayers.filter { it.teamPlayer.team == null }.getOrNull(i)
                    text(spec?.name ?: "") { color = KColors.DIMGRAY.value(); italic = true }
                }

                skin {
                    when (val spec = onlinePlayers.filter { it.teamPlayer.team == null }.getOrNull(i)) {
                        null -> SkinTexture.DARK_GRAY
                        else -> SkinTexture.PlayerSkinTexture(spec)
                    }
                }
            }
        }
    }
}

inline fun Player.setPlayerList(builder: PlayerListBodyBuilder.() -> Unit): PlayerListBody {
    return PlayerListBody().apply {
        PlayerListBodyBuilder(this).apply(builder)
        show(this@setPlayerList)
    }
}