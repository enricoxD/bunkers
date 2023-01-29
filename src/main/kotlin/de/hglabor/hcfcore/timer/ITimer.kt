package de.hglabor.hcfcore.timer

interface ITimer {
    var start: Long?
    val name: String

    fun start() {
        start = System.currentTimeMillis()
    }

    fun remainingTime(): Long

    fun onEnd()
}