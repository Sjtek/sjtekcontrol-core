package nl.sjtek.control.core.settings

data class User(val username: String, val firstName: String, val lastName: String, val playlists: List<String> = listOf())

fun User?.getDefaultPlaylist(): String = this?.playlists?.get(0) ?: "spotify:user:1133212423:playlist:2A8r6F6GiLwpBCUQ0ImYKW"