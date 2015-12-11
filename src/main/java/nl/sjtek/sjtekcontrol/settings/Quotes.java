package nl.sjtek.sjtekcontrol.settings;

import java.util.Random;

/**
 * Created by wouter on 11-12-15.
 */
public class Quotes {
    private String[] quotes = {
            "Alleen massaproductie",
            "Dien mam",
            "Mwoah, Gertje",
            "Analysamson",
            "Een frietkraam dat geen frieten verkoopt",
            "Moet hebben, afblijven",
            "Sjtek masterrace",
            "Ja joa",
            "10/10 would yolo again",
    };

    public String[] getQuotes() {
        return quotes;
    }

    public String getQuote() {
        return getQuotes()[new Random().nextInt(getQuotes().length)];
    }
}
