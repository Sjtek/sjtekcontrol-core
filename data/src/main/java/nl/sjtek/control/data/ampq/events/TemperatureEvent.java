package nl.sjtek.control.data.ampq.events;

import java.util.Locale;

/**
 * Created by wouter on 6-12-16.
 */
public class TemperatureEvent {

    private static final String MESSAGE_TEMPLATE = "%02d;%.02f;%.02f";

    private final int id;
    private final float temperature;
    private final float humidity;

    public TemperatureEvent(int id, float temperature, float humidity) {
        this.id = id;
        this.temperature = temperature;
        this.humidity = humidity;
    }

    public TemperatureEvent(int id, float temperature) {
        this(id, temperature, 0);
    }

    public TemperatureEvent(String message) {
        String[] args = message.split(";");
        if (args.length != 3) throw new IllegalArgumentException("Invalid arguments count");
        this.id = Integer.parseInt(args[0]);
        this.temperature = Float.parseFloat(args[1]);
        this.humidity = Float.parseFloat(args[2]);
    }

    public int getId() {
        return id;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, MESSAGE_TEMPLATE, id, temperature, humidity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TemperatureEvent that = (TemperatureEvent) o;

        if (id != that.id) return false;
        if (Float.compare(that.temperature, temperature) != 0) return false;
        return Float.compare(that.humidity, humidity) == 0;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (temperature != +0.0f ? Float.floatToIntBits(temperature) : 0);
        result = 31 * result + (humidity != +0.0f ? Float.floatToIntBits(humidity) : 0);
        return result;
    }
}
