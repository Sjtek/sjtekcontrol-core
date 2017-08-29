package nl.sjtek.control.core.settings

data class User(
        val username: String,
        val firstName: String,
        val lastName: String,
        val playlists: Map<String, String> = mapOf())

data class UserHolder(val users: List<User> = listOf())

fun User?.getDefaultPlaylist(): String {
    val values = this?.playlists?.values ?: listOf()
    return if (values.isEmpty()) {
        SettingsManager.settings.music.defaultPlaylist
    } else {
        values.first()
    }
}