package nl.sjtek.control.data.amqp

import java.util.*

data class SensorEvent(val type: Type, val id: Int, val value1: Float, val value2: Float = 0f) : AMQPEvent() {

    override fun toMessage(): ByteArray = if (value2 == 0f) {
        TEMPLATE1.format(Locale.US, type.ordinal, id, value1).toByteArray()
    } else {
        TEMPLATE2.format(Locale.US, type.ordinal, id, value1, value2).toByteArray()
    }

    enum class Type { MOTION, LIGHT, TEMPERATURE }

    companion object {
        const val TEMPLATE1 = "%02d;%02d;%.2f"
        const val TEMPLATE2 = "%02d;%02d;%.2f;%.2f"

        fun fromMessage(message: String): SensorEvent? {
            val args = message.split(delimiters = ";")
            return if (args.size == 3 || args.size == 4) {
                try {
                    val type = Type.values()[args[0].toInt()]
                    val id = args[1].toInt()
                    val value1 = args[2].toFloat()
                    val value2 = if (args.size == 4) args[3].toFloat() else 0f
                    SensorEvent(type, id, value1, value2)
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