package de.hglabor.hcfcore.team

interface Raidable {
    var dtr: Float
    var maxDtr: Float
    //var regenStatus: RegenStatus
    //var remainingRegenerationTime: Long
    var isRaidable: Boolean

    fun calculateMaxDtr(): Float
}