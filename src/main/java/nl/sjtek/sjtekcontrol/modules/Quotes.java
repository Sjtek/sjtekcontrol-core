package nl.sjtek.sjtekcontrol.modules;

import nl.sjtek.sjtekcontrol.utils.SettingsManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

/**
 * Created by wouter on 24-11-15.
 */
public class Quotes extends BaseModule {

    private Random random = new Random();

    public String getAll() {
        JSONArray jsonArray = new JSONArray();
        for (String quote : SettingsManager.getInstance().getQuotes().getQuotes()) {
            jsonArray.put(quote);
        }
        return jsonArray.toString();
    }

    public String getQuote() {
        String[] quotes = SettingsManager.getInstance().getQuotes().getQuotes();
        String quote;
        if (quotes.length > 0) {
            quote = quotes[random.nextInt(quotes.length)];
        } else {
            quote = "";
        }
        return quote;
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("quote", getQuote());
        return jsonObject;
    }

    @Override
    public String getSummaryText() {
        return getQuote();
    }
}
