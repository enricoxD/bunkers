package de.hglabor.hcfcore.team.data

enum class TeamRole(val prefix: String) {
    MEMBER(""), CAPTAIN("*"), LEADER("**");

    val hasBasicPermission get() = when(this) {
        CAPTAIN, LEADER -> true
        else -> false
    }

    val hasFullPermission get() = this == LEADER
}