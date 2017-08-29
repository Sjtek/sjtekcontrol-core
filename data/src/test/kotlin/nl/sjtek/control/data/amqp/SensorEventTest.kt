package nl.sjtek.control.data.amqp

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.nio.charset.Charset

internal class SensorEventTest {
    @Test
    fun toMessage() {
        val event1 = SensorEvent(SensorEvent.Type.LIGHT, 1, 2.1f)
        assertEquals("01;01;2.10", event1.toMessage().toString(Charset.defaultCharset()))
        val event2 = SensorEvent(SensorEvent.Type.MOTION, 12, 100.0f)
        assertEquals("00;12;100.00", event2.toMessage().toString(Charset.defaultCharset()))
        val event3 = SensorEvent(SensorEvent.Type.TEMPERATURE, 5, 25.123f)
        assertEquals("02;05;25.12", event3.toMessage().toString(Charset.defaultCharset()))
    }

    @Test
    fun fromMessage() {
        assertNull(SensorEvent.fromMessage(""))
        assertNull(SensorEvent.fromMessage(";;"))
        assertNull(SensorEvent.fromMessage(";"))
        assertNull(SensorEvent.fromMessage("3;0;0"))
        assertNull(SensorEvent.fromMessage("-1;0;0"))
        assertEquals(
                SensorEvent(SensorEvent.Type.MOTION, 1, 2.012f),
                SensorEvent.fromMessage("0;1;2.012"))
        assertEquals(
                SensorEvent(SensorEvent.Type.TEMPERATURE, 2, 2.044f),
                SensorEvent.fromMessage("2;00002;2.044"))
        assertEquals(
                SensorEvent(SensorEvent.Type.LIGHT, 87, -1f),
                SensorEvent.fromMessage("1;87;-1"))
    }
}