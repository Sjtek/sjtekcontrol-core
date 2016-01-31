package nl.sjtek.control.data.responses;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by wouter on 29-1-16.
 */
public class ResponseCollection {

    private static final String DEFAULT_LIGHTS = "lights";
    private static final String DEFAULT_MUSIC = "music";
    private static final String DEFAULT_NFC = "nfc";
    private static final String DEFAULT_NIGHT_MODE = "nightmode";
    private static final String DEFAULT_QUOTES = "quotes";
    private static final String DEFAULT_SONARR = "sonarr";
    private static final String DEFAULT_TEMPERATURE = "temperature";
    private static final String DEFAULT_TIME = "time";
    private static final String DEFAULT_TV = "tv";

    private final Map<String, Response> responseMap;

    public ResponseCollection(String jsonString) {
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        responseMap = new Gson().fromJson(jsonString, type);
    }

    public LightsResponse getLights() {
        return (LightsResponse) responseMap.get(DEFAULT_LIGHTS);
    }

    public MusicResponse getMusic() {
        return (MusicResponse) responseMap.get(DEFAULT_MUSIC);
    }

    public NFCResponse getNFC() {
        return (NFCResponse) responseMap.get(DEFAULT_NFC);
    }

    public NightModeResponse getNightMode() {
        return (NightModeResponse) responseMap.get(DEFAULT_NIGHT_MODE);
    }

    public QuotesResponse getQuotes() {
        return (QuotesResponse) responseMap.get(DEFAULT_QUOTES);
    }

    public SonarrResponse getSonarr() {
        return (SonarrResponse) responseMap.get(DEFAULT_SONARR);
    }

    public TemperatureResponse getTemperature() {
        return (TemperatureResponse) responseMap.get(DEFAULT_TEMPERATURE);
    }

    public TimeResponse getTime() {
        return (TimeResponse) responseMap.get(DEFAULT_TIME);
    }

    public TVResponse getTV() {
        return (TVResponse) responseMap.get(DEFAULT_TV);
    }
}
