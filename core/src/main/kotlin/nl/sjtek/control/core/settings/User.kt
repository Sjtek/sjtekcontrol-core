package nl.sjtek.control.core.settings

data class User(
        val username: String,
        val firstName: String,
        val lastName: String,
        val playlists: Map<String, String> = mapOf())

fun User?.getDefaultPlaylist(): String {
    val values = this?.playlists?.values ?: listOf()
    return if (values.isEmpty()) {
        "spotify:user:1133212423:playlist:2A8r6F6GiLwpBCUQ0ImYKW"
    } else {
        values.first()
    }
}