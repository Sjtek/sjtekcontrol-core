package nl.sjtek.control.data.amqp

import nl.sjtek.control.data.toInt

data class SwitchStateEvent(val id: Int, val state: Boolean) : AMQPEvent() {

    override fun toMessage(): ByteArray = TEMPLATE.format(id, state.toInt()).toByteArray()

    companion object {
        private const val TEMPLATE = "99;%02d;%01d"

        fun parseMessage(message: String): SwitchStateEvent? {
            val args = message.split(delimiters = ";")
            return if (args.size == 3 && args[0] == "99") {
                try {
                    val id = args[1].toInt()
                    val state = args[2] == "1"
                    SwitchStateEvent(id, state)
                } catch (e: NumberFormatException) {
                    null
                }
            } else {
                null
            }
        }
    }
}