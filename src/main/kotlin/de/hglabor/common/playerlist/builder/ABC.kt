package de.hglabor.common.playerlist.builder

import de.hglabor.bunkers.game.GameManager
import de.hglabor.bunkers.game.phase.phases.LobbyPhase
import de.hglabor.bunkers.mechanics.PlayerRespawn
import de.hglabor.bunkers.teams.BunkersTeam
import de.hglabor.bunkers.teams.TeamManager
import de.hglabor.common.playerlist.SkinTexture
import de.hglabor.common.playerlist.body.PlayerListBody
import de.hglabor.common.text.literalText
import de.hglabor.hcfcore.manager.player.teamPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.entity.Player

object ABC {
    // TODO rename class x D
    // TODO skins dont update?
    fun setPlayerList(player: Player) {
        player.setTablist {
            +column(0) {
                +entry(2) {
                    name(literalText("Team Info") { color = KColors.AQUAMARINE.value(); bold = true })
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
                    name(literalText("Game Info") { color = KColors.AQUAMARINE.value(); bold = true })
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
                        member == null -> SkinTexture.GRAY
                        bukkitPlayer != null -> SkinTexture.PlayerSkinTexture(bukkitPlayer)
                        else -> null
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

inline fun Player.setTablist(builder: PlayerListBodyBuilder.() -> Unit): PlayerListBody {
    return PlayerListBody().apply {
        PlayerListBodyBuilder(this).apply(builder)
        show(this@setTablist)
    }
}