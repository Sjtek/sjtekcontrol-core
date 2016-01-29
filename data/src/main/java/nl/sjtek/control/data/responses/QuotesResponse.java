package nl.sjtek.control.data.responses;

/**
 * Created by wouter on 28-1-16.
 */
public class QuotesResponse extends Response {

    private final String quote;

    public QuotesResponse(String quote) {
        this.quote = quote;
    }

    public String getQuote() {
        return quote;
    }
}
