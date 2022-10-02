package de.hglabor.bunkers.teams

import de.hglabor.common.extension.sendMsg
import de.hglabor.common.serialization.TextColorSerializer
import de.hglabor.hcfcore.player.impl.TeamPlayer
import de.hglabor.hcfcore.team.data.TeamRole
import de.hglabor.hcfcore.team.impl.PlayerTeam
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.serialization.ItemStackSerializer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.*

@Serializable
class BunkersTeam(
    @Transient private val _name: String = "",
    @Serializable(with = TextColorSerializer::class) val teamColor: TextColor,
    val iconMaterial: Material
): PlayerTeam(_name) {

    @Transient
    override val members: MutableSet<UUID> = mutableSetOf()

    override fun join(teamPlayer: TeamPlayer) {
        ItemStackSerializer
        val player = teamPlayer.player ?: return
        if (player.uniqueId in members) {
            player.sendMsg {
                text("You are already on this team!") { color = KColors.RED }
            }
            player.playSound(player.location, Sound.ENTITY_VILLAGER_NO, 1f, 1f)
            return
        }

        if (TeamManager.MAX_PLAYERS_PER_FACTION <= members.size) {
            player.sendMsg {
                text("The selected team is currently full.") { color = KColors.RED }
            }
            player.playSound(player.location, Sound.ENTITY_VILLAGER_NO, 1f, 1f)
            return
        }
        player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1f, 1f)
        player.closeInventory()

        teamPlayer.team?.leave(teamPlayer)

        super.join(teamPlayer)
    }

    override fun leave(teamPlayer: TeamPlayer) {
        teamPlayer.teamName = null
        teamPlayer.teamRole = null
        claim?.accessibleMembers?.remove(teamPlayer.uuid)
        members.remove(teamPlayer.uuid)
        notify {
            text("${teamPlayer.name} ") { color = KColors.DARKAQUA }
            text("left ") { color = KColors.RED }
            text("the team.") { color = KColors.GRAY }
        }
    }

    override fun sendInfo(player: Player) {
        fun List<TeamPlayer>.toComponent(): Component {
            return literalText {
                val iter = iterator()
                while (iter.hasNext()) {
                    val fp = iter.next()
                    val isOnline = fp.player != null

                    text(fp.name) {
                        color = if (isOnline) KColors.GREEN else KColors.GRAY
                    }
                    text("[") { color = KColors.DARKGRAY }
                    text("${fp.statistics.kills}") { color = KColors.LIGHTGRAY }
                    text("]") { color = KColors.DARKGRAY }

                    if (iter.hasNext()) {
                        text(",") { color = KColors.DARKGRAY }
                    }
                }
            }
        }

        val totalMembers = members.size
        val onlineMembers = players
        val members = teamPlayers.filter { m -> m.teamRole == TeamRole.MEMBER }

        val strike =
            literalText { text("-------------------------") { color = KColors.DARKGRAY; strikethrough = true } }
        val hq = if (homeLocation == null) "None" else "${homeLocation?.blockX}, ${homeLocation?.blockZ}"

        player.sendMessage(strike)

        // Show the name and the HQ of the team
        player.sendMessage(literalText {
            text("Name: ") { color = KColors.GRAY }
            text("$name ") { color = teamColor }
            text("[") { color = KColors.DARKGRAY }
            text("${onlineMembers.size}/${totalMembers}") { color = KColors.LIGHTGRAY }
            text("]") { color = KColors.DARKGRAY }
            text(" - ") { color = KColors.DARKGRAY }
            text("HQ: ") { color = KColors.GRAY }
            text(hq) { color = KColors.FLORALWHITE }
        })

        // show members
        if (members.isNotEmpty()) {
            player.sendMessage(literalText {
                text("Members: ") { color = KColors.GRAY }
                component(members.toComponent())
            })
        }

        // show dtr
        player.sendMessage(literalText {
            text("Deaths until Raidable: ") { color = KColors.GRAY }
            text("$dtr") { color = KColors.LIME }
        })

        player.sendMessage(strike)
    }

    override fun calculateMaxDtr(): Float {
        return 6.0f
    }
}