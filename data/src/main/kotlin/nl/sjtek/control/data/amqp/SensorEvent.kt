package nl.sjtek.control.data.amqp

import java.util.*

data class SensorEvent(val type: Type, val id: Int, val value: Float) : AMQPEvent() {

    override fun toMessage(): ByteArray = TEMPLATE.format(Locale.US, type.ordinal, id, value).toByteArray()

    enum class Type { MOTION, LIGHT, TEMPERATURE }

    companion object {
        const val TEMPLATE = "%02d;%02d;%.2f"

        fun fromMessage(message: String): SensorEvent? {
            val args = message.split(delimiters = ";")
            return if (args.size == 3) {
                try {
                    val type = Type.values()[args[0].toInt()]
                    val id = args[1].toInt()
                    val value = args[2].toFloat()
                    SensorEvent(type, id, value)
                } catch (e: NumberFormatException) {
                    null
                } catch (e: ArrayIndexOutOfBoundsException) {
                    null
                }
            } else {
                null
            }
        }
    }
}