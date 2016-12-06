package nl.sjtek.control.data.ampq.events;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for light events.
 */
public class LightEventTest {

    @Test
    public void testInitStateOff() {
        LightEvent event = new LightEvent(1, false);

        assertEquals(0, event.getR());
        assertEquals(0, event.getG());
        assertEquals(0, event.getB());
        assertEquals(false, event.useRgb());
        assertEquals(false, event.isEnabled());
        assertEquals(1, event.getId());
    }

    @Test
    public void testInitStateOn() {
        LightEvent event = new LightEvent(1, true);

        assertEquals(0, event.getR());
        assertEquals(0, event.getG());
        assertEquals(0, event.getB());
        assertEquals(false, event.useRgb());
        assertEquals(true, event.isEnabled());
        assertEquals(1, event.getId());
    }

    @Test
    public void testInitRgbOff() {
        LightEvent event = new LightEvent(1, 0, 0, 0);

        assertEquals(0, event.getR());
        assertEquals(0, event.getG());
        assertEquals(0, event.getB());
        assertEquals(true, event.useRgb());
        assertEquals(false, event.isEnabled());
        assertEquals(1, event.getId());
    }

    @Test
    public void testInitRgbOn() {
        LightEvent event = new LightEvent(1, 255, 0, 0);

        assertEquals(255, event.getR());
        assertEquals(0, event.getG());
        assertEquals(0, event.getB());
        assertEquals(true, event.useRgb());
        assertEquals(true, event.isEnabled());
        assertEquals(1, event.getId());
    }

    @Test
    public void testMessageState() {
        LightEvent event = new LightEvent(1, false);
        assertEquals("01;0;000;000;000", event.toString());
    }

    @Test
    public void testMessageRgbOn() {
        LightEvent event = new LightEvent(1, 255, 50, 1);
        assertEquals("01;1;255;050;001", event.toString());
    }

    @Test
    public void testMessageRgbOff() {
        LightEvent event = new LightEvent(1, 0, 0, 0);
        assertEquals("01;0;000;000;000", event.toString());
    }

    @Test
    public void testParseState() {
        String message1 = "01;1;000;000;000";
        String message2 = "01;1";
        LightEvent event1 = new LightEvent(message1);
        LightEvent event2 = new LightEvent(message2);
        assertEquals(1, event1.getId());
        assertEquals(true, event1.isEnabled());
        assertEquals(false, event1.useRgb());
        assertEquals("Check short message", event1, event2);
    }

    @Test
    public void testParseRgbOn() {
        String message1 = "01;1;255;050;001";
        LightEvent event1 = new LightEvent(message1);
        assertEquals(1, event1.getId());
        assertEquals(true, event1.isEnabled());
        assertEquals(true, event1.useRgb());
        assertEquals(255, event1.getR());
        assertEquals(50, event1.getG());
        assertEquals(1, event1.getB());
    }

    @Test
    public void testParseRgbOff() {
        String message1 = "01;0;255;050;001";
        LightEvent event1 = new LightEvent(message1);
        assertEquals(1, event1.getId());
        assertEquals(false, event1.isEnabled());
        assertEquals(true, event1.useRgb());
        assertEquals(255, event1.getR());
        assertEquals(50, event1.getG());
        assertEquals(1, event1.getB());
    }
}