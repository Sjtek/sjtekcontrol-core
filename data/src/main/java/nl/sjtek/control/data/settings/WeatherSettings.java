package nl.sjtek.control.data.settings;

/**
 * Created by wouter on 15-11-16.
 */
public class WeatherSettings extends Setting {

    private final String apiKey;

    public WeatherSettings(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }
}
