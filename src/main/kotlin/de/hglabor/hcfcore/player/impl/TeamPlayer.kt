package de.hglabor.hcfcore.player.impl

import de.hglabor.common.serialization.UUIDSerializer
import de.hglabor.common.utils.LazyMutable
import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.chat.ChatChannel
import de.hglabor.hcfcore.team.claim.IClaim
import de.hglabor.hcfcore.team.data.TeamRole
import de.hglabor.hcfcore.team.impl.PlayerTeam
import de.hglabor.hcfcore.player.ITeamPlayer
import de.hglabor.hcfcore.player.PlayerStats
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.bukkit.Bukkit
import java.util.*

@Serializable
open class TeamPlayer(
    @Serializable(with = UUIDSerializer::class) override val uuid: UUID,
    override var name: String
): ITeamPlayer {
    override var balance: Int = 200
    override val statistics: PlayerStats = PlayerStats()

    override var teamName: String? = null
        set(value) {
            if (value == null) {
                chatChannel = ChatChannel.PUBLIC
                teamRole = null
            } else {
                teamRole = TeamRole.MEMBER
            }
            field = value
            team = Core.teamManager.teamByName(teamName) as PlayerTeam?
        }
    override var teamRole: TeamRole? = null
    override var chatChannel: ChatChannel = ChatChannel.PUBLIC
    override val invitesToTeam: MutableSet<String> = mutableSetOf()

    @Transient
    var currentClaim: String? = null
    val claim: IClaim? get() = Core.claimManager.getClaimOf(currentClaim)
    var team: PlayerTeam? by LazyMutable { Core.teamManager.teamByName(teamName) as PlayerTeam? }
        private set

    val player get() = Bukkit.getPlayer(name)
}