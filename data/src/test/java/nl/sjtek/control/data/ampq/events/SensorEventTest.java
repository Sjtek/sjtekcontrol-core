package nl.sjtek.control.data.ampq.events;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by wouter on 22-3-17.
 */
public class SensorEventTest {


    private static final String MESSAGE_1 = "0;1;1";
    private static final String MESSAGE_2 = "1;2;0.453";
    private static final String MESSAGE_3 = "2;11;-0.3";
    private static final String MESSAGE_4 = "99;10;0.3";

    @Test
    public void parseMessage() throws Exception {
        SensorEvent event1 = SensorEvent.parseMessage(MESSAGE_1);
        assertNotNull(event1);
        assertEquals(SensorEvent.Type.MOTION, event1.getType());
        assertEquals(1, event1.getId());
        assertEquals(1, event1.getValue(), 0.2);

        SensorEvent event2 = SensorEvent.parseMessage(MESSAGE_2);
        assertNotNull(event2);
        assertEquals(SensorEvent.Type.LIGHT, event2.getType());
        assertEquals(2, event2.getId());
        assertEquals(0.45, event2.getValue(), 0.2);

        SensorEvent event3 = SensorEvent.parseMessage(MESSAGE_3);
        assertNotNull(event3);
        assertEquals(SensorEvent.Type.TEMPERATURE, event3.getType());
        assertEquals(11, event3.getId());
        assertEquals(-0.3, event3.getValue(), 0.2);

        SensorEvent event4 = SensorEvent.parseMessage(MESSAGE_4);
        assertNull(event4);
    }

    @Test
    public void toStringTest() throws Exception {
        SensorEvent event1 = new SensorEvent(SensorEvent.Type.MOTION, 13, 1.678f);
        assertEquals("0;13;1.68", event1.toString());

        SensorEvent event2 = new SensorEvent(SensorEvent.Type.LIGHT, 1, -1.678f);
        assertEquals("1;1;-1.68", event2.toString());

        SensorEvent event3 = new SensorEvent(SensorEvent.Type.TEMPERATURE, 30, 1337);
        assertEquals("2;30;1337.00", event3.toString());
    }
}