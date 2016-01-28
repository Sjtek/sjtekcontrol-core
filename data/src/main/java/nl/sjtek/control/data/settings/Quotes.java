package nl.sjtek.control.data.settings;

import java.util.Random;

/**
 * Created by wouter on 11-12-15.
 */
public class Quotes {
    private final String[] quotes;

    public Quotes(String[] quotes) {
        this.quotes = quotes;
    }

    public String[] getQuotes() {
        return quotes;
    }

    public String getQuote() {
        return getQuotes()[new Random().nextInt(getQuotes().length)];
    }
}
