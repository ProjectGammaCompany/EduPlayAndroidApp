package com.eduplay.moblie.repository.responseTypes

data class PlayerStats(
    val fullStats: Boolean,
    val groupEvent: Boolean,
    val users: List<StatUser>?,
    val groups: List<StatGroup>?
) {
    data class StatUser(
        val id: String, val username: String, val avatar: String?, val points: Int
    )

    data class StatGroup(
        val id: String, val name: String, val users: List<StatUser>
    )
}
