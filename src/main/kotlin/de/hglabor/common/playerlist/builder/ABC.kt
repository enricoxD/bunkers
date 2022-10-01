package de.hglabor.common.playerlist.builder

import de.hglabor.bunkers.teams.TeamManager
import de.hglabor.common.playerlist.SkinColor
import de.hglabor.common.text.literalText
import de.hglabor.hcfcore.manager.player.teamPlayer
import net.axay.kspigot.chat.KColors
import org.bukkit.entity.Player

object ABC {
    fun setPlayerList(player: Player) {
        val playerList = PlayerListBodyBuilder().apply {
            removePlayers = true
            placeholder {
                name = {
                    literalText {
                        text("Hallo") { color = (0x000000..0xFFFFFF).random() }
                    }
                }

                shouldUpdate = true
                skin = SkinColor.DARK_BLUE
            }

            +column {
                +entry {
                    shouldUpdate = true
                }
                this[5] = entry {
                    name = {
                        literalText {
                            text("Money: ") { color = KColors.GRAY.value() }
                        }
                    }

                    shouldUpdate = false
                    skin = SkinColor.Custom(
                        "ewogICJ0aW1lc3RhbXAiIDogMTYwMTU5NzczMzkyOSwKICAicHJvZmlsZUlkIiA6ICJkZTE0MGFmM2NmMjM0ZmM0OTJiZTE3M2Y2NjA3MzViYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJTUlRlYW0iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjhjYzJhZDNmYWEwMmYzMzUwMjc3YjYwM2U0ZWI2MTg1ZWFiZDQ3NDM5ZDJkZmQwZjc2MjRlNjg2MDUzZjZhYSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
                        "rp4lgczvE1ZOwW1/rhKhtzBvCFz5XolZ7BoEm3bieoDrbjMqvsrjyMDs6KdvxbOQI26JdrdWzJUm5mam47QKDes682dtqVvefD221uDRG9vb6km3Xal2B5sGqdhl9PtuQEeGNZlTI1Ip4kBHBIcugTCWV0NzpG3dTWDi/E7rIKviGRxL4hQXMysY9e8P0de8WOMeQ/X5vV0jkPgc15OLBW+k/VTvPmFGTAv/r0Bp3sRP3L36KB8auDXWpD7DTNgGuJTlDKm1/sjTRO/5+cT/5wK6Q8oxzfrnNgt81syuCwLVpt1SP18zLUdKEGTlsSV/01HLCrQusYyBwu5gxyXkpNnd2+bg4DA+DlgxApssZEYhKKR5UsgQwrcG8GU6O1Gxt73ZSjtkq31Wa2J6+RXYoFnBQC6apIcFHoRGk7FW03x7mZiGauCCNhCIJ6gmCMjml9wwbrFP3lLedIQRn+9NgckvKOtS6dCzwyf/9+A3Fl3GlPqQRhVq96MLDnA28gcYWKToDqgh/Ra0MDJ3alSEBf5MY3ayOz3EUivxQR1ClMu6i3D+aAawqGUc3pbcrNlrlvffBfMAgGc7nTvtWakloqWlE/Xu4CcZ/fK0BIFGf3MD77CRJHF/MfVQFYkkGyuh5zbd/qEmrKaCKvxavopFallTr89zasWbg51nlhA7lgU="
                    )
                }
                this[6] = entry {
                    name = {
                        literalText {
                            text("$${player.teamPlayer.balance}") { color = KColors.GREEN.value() }
                        }
                    }

                    shouldUpdate = true
                    skin = SkinColor.Custom(
                        "ewogICJ0aW1lc3RhbXAiIDogMTYwMTU5NzczMzkyOSwKICAicHJvZmlsZUlkIiA6ICJkZTE0MGFmM2NmMjM0ZmM0OTJiZTE3M2Y2NjA3MzViYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJTUlRlYW0iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjhjYzJhZDNmYWEwMmYzMzUwMjc3YjYwM2U0ZWI2MTg1ZWFiZDQ3NDM5ZDJkZmQwZjc2MjRlNjg2MDUzZjZhYSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
                        "rp4lgczvE1ZOwW1/rhKhtzBvCFz5XolZ7BoEm3bieoDrbjMqvsrjyMDs6KdvxbOQI26JdrdWzJUm5mam47QKDes682dtqVvefD221uDRG9vb6km3Xal2B5sGqdhl9PtuQEeGNZlTI1Ip4kBHBIcugTCWV0NzpG3dTWDi/E7rIKviGRxL4hQXMysY9e8P0de8WOMeQ/X5vV0jkPgc15OLBW+k/VTvPmFGTAv/r0Bp3sRP3L36KB8auDXWpD7DTNgGuJTlDKm1/sjTRO/5+cT/5wK6Q8oxzfrnNgt81syuCwLVpt1SP18zLUdKEGTlsSV/01HLCrQusYyBwu5gxyXkpNnd2+bg4DA+DlgxApssZEYhKKR5UsgQwrcG8GU6O1Gxt73ZSjtkq31Wa2J6+RXYoFnBQC6apIcFHoRGk7FW03x7mZiGauCCNhCIJ6gmCMjml9wwbrFP3lLedIQRn+9NgckvKOtS6dCzwyf/9+A3Fl3GlPqQRhVq96MLDnA28gcYWKToDqgh/Ra0MDJ3alSEBf5MY3ayOz3EUivxQR1ClMu6i3D+aAawqGUc3pbcrNlrlvffBfMAgGc7nTvtWakloqWlE/Xu4CcZ/fK0BIFGf3MD77CRJHF/MfVQFYkkGyuh5zbd/qEmrKaCKvxavopFallTr89zasWbg51nlhA7lgU="
                    )
                }
            }

            +column {
                this[3] = entry {
                    name = {
                        literalText {
                            text("Team Red") { color = KColors.RED.value(); bold = true }
                        }
                    }

                    shouldUpdate = false
                    skin = SkinColor.RED
                }

                for (i in 0.. TeamManager.MAX_PLAYERS_PER_FACTION) {
                    this[3+i] = entry {
                        name = {
                            val member = TeamManager.red.players.getOrNull(i)
                            literalText {
                                text(member?.name ?: "") { color = KColors.RED.value() }
                            }
                        }

                        shouldUpdate = true
                        skin = SkinColor.DARK_RED
                    }
                }
            }
        }

        playerList.body.show(player)
    }
}