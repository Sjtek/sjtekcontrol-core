package nl.sjtek.control.data.staticdata

import java.io.Serializable

data class User(
        val username: String,
        val firstName: String,
        val lastName: String,
        val playlists: Map<String, String> = mapOf()) : Serializable

