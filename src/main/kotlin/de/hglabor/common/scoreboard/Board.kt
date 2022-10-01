package de.hglabor.auseinandersetzung.common.scoreboard

import net.axay.kspigot.chat.KColors
import net.axay.kspigot.chat.literalText
import net.axay.kspigot.runnables.KSpigotRunnable
import net.axay.kspigot.runnables.task
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Criterias
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Team

class Board(var updatingPeriod: Long = 20L) {
    var lines = mutableListOf<BoardLine>()
    var title: Component = Component.empty()
        set(value) {
            field = value
            objective.displayName(value)
        }
    val scoreboard = Bukkit.getScoreboardManager().newScoreboard
    val objective: Objective = scoreboard.registerNewObjective("aaa", "bbb", title)
    var runnable: KSpigotRunnable? = null

    init {
        objective.displaySlot = DisplaySlot.SIDEBAR
        startRunnable()
    }

    private fun startRunnable() {
        runnable = task(true, 20L, updatingPeriod) {
            updateBoard()
        }
    }

    fun updateBoard() {
        lines.filter { it.shouldUpdate }.forEach { it.update() }
    }

    fun resetBoard() {
        lines.forEach { it.unregister() }
        lines.forEach { it.register() }
    }

    fun addLine(line: Int = -1, boardLine: BoardLine) {
        if (line == -1) {
            lines.add(boardLine)
            boardLine.register()
        } else {
            lines.forEach { it.unregister() }
            lines.add(line, boardLine)
            lines.forEach { it.register() }
        }
    }

    fun addLine(line: Int = -1, textCallback: () -> Component) {
        addLine(line, BoardLine(textCallback))
    }

    fun addLine(line: Int = -1, text: Component) {
        addLine(line, BoardLine(text))
    }

    fun addLineBelow(textCallback: () -> Component) {
        addLine(0, BoardLine(textCallback))
    }

    fun addLineBelow(text: Component) {
        addLine(0, BoardLine(text))
    }

    fun getLine(line: Int) = lines.getOrNull(line)

    fun setLine(line: Int, text: Component) {
        val l = getLine(line) ?: error("Line $line not found!")
        l.shouldUpdate = false
        l.set(text)
    }

    fun setLine(line: Int, textCallback: () -> Component) {
        val l = getLine(line) ?: error("Line $line not found!")
        l.shouldUpdate = true
        l.set(textCallback)
    }

    fun deleteLine(boardLine: BoardLine) {
        lines -= boardLine
        boardLine.apply {
            shouldUpdate = false
            team.unregister()
            scoreboard.resetScores(entry)
        }
        resetBoard()
    }

    fun deleteLine(line: Int) {
        deleteLine(lines[line])
    }

    fun clear() {
        lines.forEach { it.unregister() }
    }

    fun delete() {
        clear()
        runnable?.cancel()
    }

    fun addFlag(boardFlag: BoardFlag) {
        when (boardFlag) {
            BoardFlag.SHOW_HEALTH -> {
                scoreboard.registerNewObjective("showhealth", Criterias.HEALTH, Criterias.HEALTH).apply {
                    displaySlot = DisplaySlot.BELOW_NAME
                    displayName(literalText("\u2764") { color = KColors.RED })
                    getScore("${ChatColor.WHITE}").score = 0
                }
            }
        }
    }

    fun setScoreboard(player: Player): Board {
        player.scoreboard = scoreboard
        return this
    }

    inner class BoardLine(var textCallback: () -> Component) {
        constructor(text: Component) : this({ text }) {
            shouldUpdate = false
        }

        var shouldUpdate: Boolean = true
        var team: Team = scoreboard.getTeam("placeholder") ?: scoreboard.registerNewTeam("placeholder")
        lateinit var entry: String

        fun register() {
            val index = lines.indexOf(this)
            team = scoreboard.getTeam("$index") ?: scoreboard.registerNewTeam("$index")
            entry = entry(index)
            team.addEntry(entry)
            team.prefix(text)
            objective.getScore(entry).score = index
        }

        fun unregister() {
            val newLines = lines.toMutableList()
            newLines -= this
            lines = newLines
            scoreboard.resetScores(entry)
        }

        fun update() {
            team.prefix(text)
        }

        fun set(textCallback: () -> Component) {
            this.textCallback = textCallback
            shouldUpdate = true
        }

        fun set(text: Component) {
            this.textCallback = { text }
            shouldUpdate = false
        }

        val text: Component get() = textCallback.invoke()
    }

    private fun entry(index: Int) = "${ChatColor.values()[index]}${ChatColor.WHITE}"
}
