package de.hglabor.hcfcore.timer

interface ITimer {
    var start: Long?
    val name: String

    fun remainingTime(): Long

    fun onEnd()
}