package de.hglabor.hcfcore.player

import de.hglabor.hcfcore.chat.ChatChannel
import de.hglabor.hcfcore.team.data.TeamRole
import java.util.*

interface ITeamPlayer {
   val uuid: UUID
   var name: String
   var balance: Int
   val statistics: PlayerStats

   var teamName: String?
   var teamRole: TeamRole?
   var chatChannel: ChatChannel
   val invitesToTeam: MutableSet<String>
}