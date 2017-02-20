package nl.sjtek.control.data.ampq.events;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TemperatureEventTest {

    @Test
    public void testInitTemperature() {
        TemperatureEvent event = new TemperatureEvent(1, 20.1f);
        assertEquals(1, event.getId());
        assertEquals(20.1f, event.getTemperature(), 0.01);
        assertEquals(0, event.getHumidity(), 0.01);
    }

    @Test
    public void testInitHumidity() {
        TemperatureEvent event = new TemperatureEvent(1, 20.1f, 0.67f);
        assertEquals(1, event.getId());
        assertEquals(20.1f, event.getTemperature(), 0.01);
        assertEquals(0.67f, event.getHumidity(), 0.01);
    }

    @Test
    public void testMessage() {
        TemperatureEvent event = new TemperatureEvent(1, 1.1f, 0.67f);
        assertEquals("01;1.10;0.67", event.toString());
    }

    @Test
    public void testParseMessage() {
        TemperatureEvent event = new TemperatureEvent("02;20.1;0.42");
        assertEquals(2, event.getId());
        assertEquals(20.1f, event.getTemperature(), 0.01);
        assertEquals(0.42f, event.getHumidity(), 0.01);
    }

    @Test
    public void testEquals() {
        TemperatureEvent event1 = new TemperatureEvent(2, 20.1f, 0.10f);
        TemperatureEvent event2 = new TemperatureEvent(2, 20.1f, 0.10f);
        assertEquals(true, event1.equals(event2));
        assertEquals(event1.hashCode(), event2.hashCode());
    }
}