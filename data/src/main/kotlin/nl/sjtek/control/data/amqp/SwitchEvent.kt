package nl.sjtek.control.data.amqp

import nl.sjtek.control.data.toInt

data class SwitchEvent(val id: Int, val state: Boolean, val r: Int = -1, val g: Int = -1, val b: Int = -1) : AMQPEvent() {
    val useRgb: Boolean
        get() = r >= 0 && g >= 0 && b >= 0

    override fun toMessage(): ByteArray = if (useRgb) {
        TEMPLATE_RGB.format(id, state.toInt(), r, g, b).toByteArray()
    } else {
        TEMPLATE_STATE.format(id, state.toInt()).toByteArray()
    }

    companion object {
        private const val TEMPLATE_RGB = "%02d;%01d;%03d;%03d;%03d"
        private const val TEMPLATE_STATE = "%02d;%01d"

        fun fromMessage(message: String): SwitchEvent? {
            val args = message.split(delimiters = ";")
            if (args.size >= 2) {
                try {
                    val id = args[0].toInt()
                    val state = args[1] == "1"

                    return if (args.size == 5) {
                        val r = args[2].toInt()
                        val g = args[3].toInt()
                        val b = args[4].toInt()
                        SwitchEvent(id, state, r, g, b)
                    } else {
                        SwitchEvent(id, state)
                    }

                } catch (e: NumberFormatException) {
                    return null
                }
            } else {
                return null
            }
        }
    }
}