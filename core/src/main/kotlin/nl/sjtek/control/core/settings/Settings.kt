package nl.sjtek.control.core.settings

data class Settings(
        val spark: Boolean = true,
        val tv: TV = TV(),
        val amqp: AMQP = AMQP(),
        val music: Music = Music(),
        val temperature: Temperature = Temperature()) {

    data class TV(
            val ip: String = "10.10.0.20",
            val port: Int = 8080,
            val pin: String = "000000",
            val protocol: String = "roap",
            val scriptPath: String = "/usr/bin/lgcommander")

    data class AMQP(
            val host: String = "10.10.0.1",
            val username: String = "core",
            val password: String = "yolo")

    data class Music(
            val url: String = "ws://127.0.0.1:6680/mopidy/ws",
            val volume: Int = 5,
            val defaultPlaylist: String = "spotify:user:1133212423:playlist:2A8r6F6GiLwpBCUQ0ImYKW")

    data class Temperature(
            val apiKey: String = "",
            val urlOutside: String = "https://api.darksky.net/forecast/%s/51.5121298,5.4924242",
            val insideSensorId: Int = 1)
}