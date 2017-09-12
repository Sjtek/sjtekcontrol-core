package nl.sjtek.control.data.actions

object Actions {
    fun toggle(): String = "toggle"
    fun info(): String = "info"
    fun state(): String = "state"
    fun users(): String = "users"
    fun quotes(): String = "quotes"
    fun lamps(): String = "lamps"
    val coffee: Coffee = Coffee
    val lights: Lights = Lights
    val music: Music = Music
    val nightMode: NightMode = NightMode
    val tv: TV = TV
    val color: Color = Color

    object Coffee {
        fun enable(): String = "coffee/enable"
    }

    object Lights {
        fun toggle(id: Int = -1, room: String = "", enabled: Boolean? = null, red: Int = -1, green: Int = -1, blue: Int = -1): String {
            val query = if (red != -1 && green != -1 && blue != -1) {
                "r=$red&g=$green&b=$blue"
            } else {
                ""
            }
            val postFix = when (enabled) {
                true -> "on"
                false -> "off"
                else -> "toggle"
            }
            return when {
                room.isNotBlank() -> "room/$room/$postFix?$query"
                id != -1 -> "lights/$id/$postFix?$query"
                else -> ""
            }
        }
    }

    object Music {
        fun toggle(): String = "music/toggle"
        fun play(): String = "music/play"
        fun pause(): String = "music/pause"
        fun stop(): String = "music/stop"
        fun next(): String = "music/next"
        fun previous(): String = "music/previous"
        fun clear(): String = "music/clear"
        fun shuffle(): String = "music/shuffle"
        fun volumeIncrease(): String = "music/volume?increase"
        fun volumeLower(): String = "music/volume?lower"
        fun volume(value: Int): String = "music/volume?value=$value"
        fun start(uri: String = "", clear: Boolean = false, shuffle: Boolean = false, resetVolume: Boolean = false, userName: String = ""): String {
            var query = ""
            if (uri.isNotBlank()) query += query.appendQuery("uri=$uri")
            if (clear) query += query.appendQuery("clear")
            if (shuffle) query += query.appendQuery("shuffle")
            if (resetVolume) query += query.appendQuery("reset")
            if (userName.isNotBlank()) query += query.appendQuery("user=$userName")
            return "music/start?$query"
        }
    }

    object NightMode {
        fun enable(): String = "nightmode/enable"
        fun disable(): String = "nightmode/disable"
        fun toggle(): String = "nightmode/toggle"
    }

    object TV {
        fun turnOff(): String = "tv/turnoff"
    }

    object Color {
        fun disable(): String = "color/disable"
        fun enable(vararg lamps: Int): String = "/color/enable?lamps=${lamps.joinToString(separator = ",")}"
    }

    private fun String.appendQuery(value: String): String = if (this.isBlank()) {
        value
    } else {
        "&$value"
    }
}


