package nl.sjtek.control.core.modules;

import nl.sjtek.control.core.settings.SettingsManager;
import nl.sjtek.control.data.responses.QuotesResponse;
import nl.sjtek.control.data.responses.Response;
import org.json.JSONArray;

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
    public Response getResponse() {
        return new QuotesResponse(getQuote());
    }

    @Override
    public String getSummaryText() {
        return getQuote();
    }
}
