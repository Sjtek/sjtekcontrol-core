package nl.sjtek.control.data.ampq.events;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by wouter on 15-3-17.
 */
public class LightStateEventTest {

    private static final String MESSAGE_1 = "99;01;1";
    private static final String MESSAGE_2 = "99;11;0";
    private static final String MESSAGE_3 = "1;11;0";

    @Test
    public void parseMessage() throws Exception {
        LightStateEvent event1 = LightStateEvent.parseMessage(MESSAGE_1);
        assertNotNull(event1);
        assertEquals(1, event1.getId());
        assertEquals(true, event1.isEnabled());

        LightStateEvent event2 = LightStateEvent.parseMessage(MESSAGE_2);
        assertNotNull(event2);
        assertEquals(11, event2.getId());
        assertEquals(false, event2.isEnabled());

        LightStateEvent event3 = LightStateEvent.parseMessage(MESSAGE_3);
        assertNull(event3);
    }

    @Test
    public void toStringTest() throws Exception {
        LightStateEvent event1 = new LightStateEvent(1, true);
        assertEquals("99;01;1", event1.toString());

        LightStateEvent event2 = new LightStateEvent(14, false);
        assertEquals("99;14;0", event2.toString());
    }

}