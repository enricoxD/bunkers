package de.hglabor.hcfcore.team.impl

import de.hglabor.common.extension.sendMsg
import de.hglabor.common.serialization.UUIDSerializer
import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.team.claim.impl.TeamClaim
import de.hglabor.hcfcore.configuration.TConfig
import de.hglabor.hcfcore.team.ITeam
import de.hglabor.hcfcore.team.Raidable
import de.hglabor.hcfcore.team.data.TeamRole
import de.hglabor.hcfcore.manager.player.teamPlayerByUUID
import de.hglabor.hcfcore.player.impl.TeamPlayer
import kotlinx.serialization.Serializable
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.LiteralTextBuilder
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.runnables.async
import net.axay.kspigot.serialization.LocationSerializer
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

@Serializable
open class PlayerTeam(override var name: String) : ITeam, Raidable {

    open val members: MutableSet<@Serializable(with = UUIDSerializer::class) UUID> = mutableSetOf()
    open var balance: Int = 0

    open override var dtr: Float = calculateMaxDtr()
        set(value) {
            field = if (value > maxDtr) maxDtr
            else value
        }
        get() = BigDecimal(field.toDouble()).setScale(2, RoundingMode.HALF_UP).toFloat()

    open override var maxDtr: Float = calculateMaxDtr()

    //override var regenStatus: RegenStatus = RegenStatus.FULL
    //override var remainingRegenerationTime: Long = 0
    open override var isRaidable: Boolean = false

    @Serializable(with = LocationSerializer::class)
    open var homeLocation: Location? = null

    open val teamPlayers get() = members.mapNotNull { uuid -> teamPlayerByUUID(uuid) }
    open val players get() = members.mapNotNull { uuid -> Bukkit.getPlayer(uuid) }
    open override val claim: TeamClaim? get() = Core.claimManager.getClaimOf(this.name) as TeamClaim?

    open override fun sendInfo(player: Player) {
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
        val leader = teamPlayers.filter { m -> m.teamRole == TeamRole.LEADER }
        val captains = teamPlayers.filter { m -> m.teamRole == TeamRole.CAPTAIN }
        val members = teamPlayers.filter { m -> m.teamRole == TeamRole.MEMBER }

        val strike =
            literalText { text("-------------------------") { color = KColors.DARKGRAY; strikethrough = true } }
        val hq = if (homeLocation == null) "None" else "${homeLocation?.blockX}, ${homeLocation?.blockZ}"

        player.sendMessage(strike)

        // Show the name and the HQ of the team
        player.sendMessage(literalText {
            text("Name: ") { color = KColors.GRAY }
            text("$name ") { color = KColors.AQUAMARINE }
            text("[") { color = KColors.DARKGRAY }
            text("${onlineMembers.size}/${totalMembers}") { color = KColors.LIGHTGRAY }
            text("]") { color = KColors.DARKGRAY }
            text(" - ") { color = KColors.DARKGRAY }
            text("HQ: ") { color = KColors.GRAY }
            text(hq) { color = KColors.FLORALWHITE }
        })

        // Show the leader
        if (leader.isNotEmpty()) {
            player.sendMessage(literalText {
                text("Leader: ") { color = KColors.GRAY }
                component(leader.toComponent())
            })
        }

        // show captains
        if (captains.isNotEmpty()) {
            player.sendMessage(literalText {
                text("Captains: ") { color = KColors.GRAY }
                component(captains.toComponent())
            })
        }

        // show members
        if (members.isNotEmpty()) {
            player.sendMessage(literalText {
                text("Members: ") { color = KColors.GRAY }
                component(members.toComponent())
            })
        }

        // show balance
        player.sendMessage(literalText {
            text("Balance: ") { color = KColors.GRAY }
            text("$$balance") { color = KColors.AQUA }
        })

        // show dtr
        player.sendMessage(literalText {
            text("Deaths until Raidable: ") { color = KColors.GRAY }
            text("$dtr") { color = KColors.LIME }
        })

        player.sendMessage(strike)
    }

    open override fun disband() {
        Core.teamManager.cache.remove(name)
        balance = 0
        dtr = 0.0f
        claim?.let { claim ->
            Core.claimManager.unclaim(claim)
        }
        homeLocation = null

        async {
            teamPlayers.forEach { fp ->
                fp.teamName = null
                fp.teamRole = null
            }

            Core.playerManager.teamPlayers.filter { fp ->
                this.name in fp.invitesToTeam
            }.forEach { fp ->
                fp.invitesToTeam.remove(this.name)
            }
            notify {
                text("The team has been disbanded!") { color = KColors.RED; bold = true }
            }
            members.clear()
        }
    }

    open fun invite(player: Player, invited: TeamPlayer) {
        invited.invitesToTeam.add(name)

        notify {
            text("${invited.name} ") { color = KColors.DARKAQUA }
            text("has been ") { color = KColors.GRAY }
            text("invited to the team!") { color = KColors.AQUAMARINE }
        }
        invited.sendMsg {
            text("${player.name} ") { color = KColors.DARKAQUA }
            text("invited ") { color = KColors.LIME }
            text("you to join '") { color = KColors.GRAY }
            text(name) { color = KColors.DARKAQUA }
            text("'") { color = KColors.GRAY }
            newLine()
            text("Type '") { color = KColors.GRAY }
            text("/team join ${name}") { color = KColors.DARKAQUA }
            text("' or ") { color = KColors.GRAY }
            text("click here ") {
                color = KColors.DARKAQUA
                hoverText { text("Join ${name}") { color = KColors.LIME } }
                onClickCommand("/team join ${name}")
            }
            text("to join.") { color = KColors.GRAY }
        }
    }

    open fun join(teamPlayer: TeamPlayer) {
        members += teamPlayer.uuid
        teamPlayer.invitesToTeam -= name
        teamPlayer.teamName = name
        claim?.accessibleMembers?.add(teamPlayer.uuid)
        notify {
            text("${teamPlayer.name} ") { color = KColors.DARKAQUA }
            text("joined ") { color = KColors.LIME }
            text("the team.") { color = KColors.GRAY }
        }
        updateDtr()
    }

    open fun setHome(player: TeamPlayer) {
        val bukkitPlayer = player.player ?: return
        notify {
            text("${player.name} ") { color = KColors.AQUAMARINE }
            text("has updated the ") { color = KColors.GRAY }
            text("team's HQ point") { color = KColors.DARKAQUA }
            text("!") { color = KColors.GRAY }
        }
        homeLocation = bukkitPlayer.location
    }

    open fun leave(teamPlayer: TeamPlayer) {
        val teamRole = teamPlayer.teamRole
        // Player is leader
        if (teamRole == TeamRole.LEADER) {
            // not solo
            if (members.size > 1) {
                teamPlayer.sendMsg {
                    text("Please choose a new leader before leaving your team!") { color = KColors.RED }
                }
                return

                // Solo
            } else {
                teamPlayer.sendMsg {
                    text("Successfully left and disbanded the team!") { color = KColors.LIME }
                }
                disband()
                return
            }
        }

        teamPlayer.sendMsg {
            text("Successfully left the team!") { color = KColors.LIME }
        }
        teamPlayer.teamName = null
        teamPlayer.teamRole = null
        claim?.accessibleMembers?.remove(teamPlayer.uuid)
        members.remove(teamPlayer.uuid)
        notify {
            text("${teamPlayer.name} ") { color = KColors.DARKAQUA }
            text("left ") { color = KColors.RED }
            text("the team.") { color = KColors.GRAY }
        }
        updateDtr()
    }

    open fun updateDtr() {
        // check if regen
        maxDtr = calculateMaxDtr()
        if (dtr > maxDtr) dtr = maxDtr
    }

    open fun transfer(from: TeamPlayer, to: TeamPlayer) {
        from.teamRole = TeamRole.CAPTAIN
        to.teamRole = TeamRole.LEADER
        notify {
            text("The team has been ") { color = KColors.GRAY }
            text("transfered ") { color = KColors.DARKAQUA }
            text("to ") { color = KColors.GRAY }
            text(to.name) { color = KColors.AQUAMARINE }
            text(".") { color = KColors.GRAY }
        }
    }

    open fun notify(showPrefix: Boolean = true, message: Component) {
        if (showPrefix) players.forEach { player -> player.sendMsg(message) }
        else players.forEach { player -> player.sendMessage(message) }
    }

    open fun notify(showPrefix: Boolean = true, builder: LiteralTextBuilder.() -> Unit) = notify(showPrefix, LiteralTextBuilder("").apply(builder).build())

    override fun calculateMaxDtr(): Float {
        if (members.size == 1) return 1.01f
        if (members.size == 2) return 2.1f
        return TConfig.MAX_DTR.coerceAtMost(members.size * 0.9f);
    }
}