package nl.sjtek.control.data.static

data class User(
        val username: String,
        val firstName: String,
        val lastName: String,
        val playlists: Map<String, String> = mapOf())

