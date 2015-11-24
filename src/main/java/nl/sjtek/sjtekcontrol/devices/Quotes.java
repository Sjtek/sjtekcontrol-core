package nl.sjtek.sjtekcontrol.devices;

import nl.sjtek.sjtekcontrol.data.Settings;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by wouter on 24-11-15.
 */
public class Quotes {

    private Random random = new Random();

    @Override
    public String toString() {
        String[] quotes = Settings.getInstance().getQuotes();
        String quote = quotes[random.nextInt(quotes.length)];
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("quote", quote);
        return jsonObject.toString();
    }
}
