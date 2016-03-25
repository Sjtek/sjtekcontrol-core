package nl.sjtek.control.data.responses;

/**
 * Created by wouter on 28-1-16.
 */
public class TemperatureResponse extends Response {

    private final float outside;
    private final float inside;
    private final float humidity;
    private final String description;
    private final String icon;

    public TemperatureResponse(float outside, float inside, float humidity, String description, String icon) {
        type = this.getClass().getCanonicalName();
        this.outside = outside;
        this.inside = inside;
        this.humidity = humidity;
        this.description = description;
        this.icon = icon;
    }

    public float getOutside() {
        return outside;
    }

    public float getInside() {
        return inside;
    }

    public float getHumidity() {
        return humidity;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }
}
