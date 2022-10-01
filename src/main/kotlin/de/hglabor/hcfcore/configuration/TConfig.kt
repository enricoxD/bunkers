package de.hglabor.hcfcore.configuration

object TConfig {
    object ROOT {
        const val GENERAL = "general"
        const val FACTION = "team"
        const val WORLD = "world"
        const val CLAIM = "claim"
        const val EXPLOIT = "exploit"
    }
    // General
    val CACHING_METHOD by ConfigDelegate("memory", ROOT.GENERAL, "Possible methods: memory, mongodb, flatfile")

    // Team
    val PLAYERS_PER_FACTION by ConfigDelegate(10, ROOT.FACTION)
    val MAX_DTR by ConfigDelegate(6.0f, ROOT.FACTION)
    val FACTION_MIN_NAME_LENGTH by ConfigDelegate(2, ROOT.FACTION)
    val FACTION_MAX_NAME_LENGTH by ConfigDelegate(12, ROOT.FACTION)

    // World
    val OVERWORLD_SIZE by ConfigDelegate(2000, ROOT.WORLD)
    val NETHER_SIZE by ConfigDelegate(1000, ROOT.WORLD)

    // Claim
    val MIN_CLAIM_RADIUS by ConfigDelegate(5, ROOT.CLAIM)
    val PRICE_PER_BLOCK by ConfigDelegate(0.25, ROOT.CLAIM) // the price of one block (before multiplying)
    val BLOCKS_UNTIL_MULTIPLIER_INCREASES by ConfigDelegate(1000, ROOT.CLAIM) // how many blocks are needed until the multiplier is increased
    val SELL_MULTIPLIER by ConfigDelegate(80, ROOT.CLAIM) // the percentage of the price to be returned on sell
    val F_MAP_RADIUS by ConfigDelegate(32, ROOT.CLAIM) // the distance to show claims

    // Exploit
    val FIX_PEARL_GLITCHING by ConfigDelegate(true, ROOT.EXPLOIT)
    val REFUND_PEARL_GLITCHING by ConfigDelegate(true, ROOT.EXPLOIT)
}