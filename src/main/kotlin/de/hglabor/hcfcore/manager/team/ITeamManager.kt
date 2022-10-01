package de.hglabor.hcfcore.manager.team

import de.hglabor.hcfcore.Core
import de.hglabor.hcfcore.team.ITeam
import de.hglabor.hcfcore.team.data.TeamRole
import de.hglabor.hcfcore.team.impl.PlayerTeam
import de.hglabor.hcfcore.manager.IManager
import de.hglabor.hcfcore.manager.player.teamPlayer
import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.event.listen
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent

interface ITeamManager: IManager<String, ITeam> {

    override fun enable() {
        listen<EntityDamageByEntityEvent>(priority = EventPriority.HIGHEST) {
            if (it.isCancelled) return@listen
            val damager = it.damager as? Player ?: return@listen
            val entity = it.entity as? Player ?: return@listen
            val team = damager.teamPlayer.teamName ?: return@listen

            if (entity.teamPlayer.teamName == team) {
                it.isCancelled = true
                damager.sendMessage(literalText {
                    text("You cannot hurt ") { color = KColors.GRAY }
                    text(entity.name) { color = KColors.AQUAMARINE }
                    text(".") { color = KColors.GRAY }
                })
            }
        }

        listen<PlayerDeathEvent>(priority = EventPriority.HIGHEST) {
            if (it.isCancelled) return@listen

            it.player.teamPlayer.team?.let { team ->
                team.dtr -= 1
                team.notify(false) {
                    text("Member Death: ") { color = KColors.RED }
                    text(it.player.name)
                    newLine()
                    text("DTR: ") { color = KColors.RED }
                    text(team.dtr.toString())
                }

                if (team.dtr <= 0.0 && !team.isRaidable) {
                    team.isRaidable = true
                    team.notify {
                        text("You are now ") { color = KColors.GRAY }
                        text("raidable") { color = KColors.RED; bold = true }
                        text("!") { color = KColors.GRAY }
                        newLine()
                        text("From now on, your death will be permanent.") { color = KColors.RED }
                    }
                }
            }
        }
    }

    val teams: List<ITeam> get() = cache.values.toList()

    fun registerTeam(team: ITeam) {
        cache[team.name] = team
    }

    fun createTeam(leader: Player, name: String) {
        val teamPlayer = leader.teamPlayer
        val team = PlayerTeam(name)
        registerTeam(team)
        teamPlayer.teamName = name
        team.members += teamPlayer.uuid
        teamPlayer.teamRole = TeamRole.LEADER
    }

    fun renameTeam(team: PlayerTeam, newName: String) {
        val claim = Core.claimManager.getClaimOf(team)
        if (claim != null) {
            Core.claimManager.rename(claim, newName)
        }
        team.name = newName
    }

    fun teamByName(name: String?): ITeam? {
        return cache[name]
    }

    fun isNameFree(name: String): Boolean {
        return !cache.keys.contains(name)
    }
}