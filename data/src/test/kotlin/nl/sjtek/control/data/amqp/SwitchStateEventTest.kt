package nl.sjtek.control.data.amqp

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class SwitchStateEventTest {
    @Test
    fun fromMessage() {
        assertNull(SwitchStateEvent.parseMessage(""))
        assertNull(SwitchStateEvent.parseMessage(";"))
        assertNull(SwitchStateEvent.parseMessage(";;"))
        assertNull(SwitchStateEvent.parseMessage("01;01;01"))
        assertNull(SwitchStateEvent.parseMessage("-1;0;0"))
        assertEquals(SwitchStateEvent(1, false), SwitchStateEvent.parseMessage("99;00001;2"))
        assertEquals(SwitchStateEvent(1, true), SwitchStateEvent.parseMessage("99;00001;1"))
        assertEquals(SwitchStateEvent(1, false), SwitchStateEvent.parseMessage("99;1;0"))
    }

    @Test
    fun toMessage() {

    }
}