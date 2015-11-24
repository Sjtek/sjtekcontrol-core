package nl.sjtek.sjtekcontrol.devices;

import nl.sjtek.sjtekcontrol.data.Settings;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by wouter on 24-11-15.
 */
public class Quotes {

    private Random random = new Random();

    public String getAll() {
        JSONArray jsonArray = new JSONArray();
        for (String quote : Settings.getInstance().getQuotes()) {
            jsonArray.put(quote);
        }
        return jsonArray.toString();
    }

    @Override
    public String toString() {
        String[] quotes = Settings.getInstance().getQuotes();
        String quote;
        if (quotes.length > 0) {
            quote = quotes[random.nextInt(quotes.length)];
        } else {
            quote = "";
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("quote", quote);
        return jsonObject.toString();
    }
}
