package nl.sjtek.control.data.actions;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for building URL parameters for API calls.
 */
public class Arguments implements Serializable {

    private static final String ENCODING = "UTF-8";

    private boolean useVoice;
    private String user;
    private String url;

    public Arguments() {

    }

    /**
     * Get empty arguments.
     *
     * @return Empty arguments
     */
    public static Arguments empty() {
        return new Arguments();
    }

    /**
     * Should use voice with the call.
     *
     * @return Use voice
     */
    public boolean isUseVoice() {
        return useVoice;
    }

    /**
     * Let the API speak in the living room on receiving a certain request.
     *
     * @param useVoice Should use voice
     * @return Arguments
     */
    public Arguments setUseVoice(boolean useVoice) {
        this.useVoice = useVoice;
        return this;
    }

    /**
     * Get the user.
     *
     * @return User
     */
    public String getUser() {
        try {
            return URLDecoder.decode(user, ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Set the user for identifying with the API.
     *
     * @param user User
     * @return Arguments
     */
    public Arguments setUser(String user) {
        if (user == null) user = "";
        try {
            this.user = URLEncoder.encode(user, ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Get the url for starting a specific playlist/song.
     *
     * @return Url of the playlist/song
     */
    public String getUrl() {
        try {
            return URLDecoder.decode(url, ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Set an url for a music call to specifically start a playlist/song.
     *
     * @param url Url of the playlist/song.
     * @return Arguments
     */
    public Arguments setUrl(String url) {
        if (url == null) url = "";
        try {
            this.url = URLEncoder.encode(url, ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Build the arguments to a url encoded string for the url.
     *
     * @return Url encoded arguments
     */
    public String build() {
        List<String> argumentsList = new ArrayList<>();
        if (useVoice) argumentsList.add("voice");
        if (!isEmpty(url)) argumentsList.add("url=" + url);
        argumentsList.add("user=" + (isEmpty(user) ? "default" : user));
        return "?" + join("&", argumentsList);
    }


    /**
     * Check if a string is empty (null or length 0).
     *
     * @param text Text to check
     * @return Is empty
     */
    public boolean isEmpty(String text) {
        return text == null || text.trim().length() == 0;
    }

    /**
     * Returns a string containing the tokens joined by delimiters.
     *
     * @param delimiter Delimiter
     * @param tokens    Array of Strings to be joined.
     * @return Joined string
     */
    public String join(String delimiter, List<String> tokens) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean firstTime = true;
        for (String token : tokens) {
            if (firstTime) {
                firstTime = false;
            } else {
                stringBuilder.append(delimiter);
            }
            stringBuilder.append(token);
        }
        return stringBuilder.toString();

    }

    /**
     * {@see #build()}
     */
    @Override
    public String toString() {
        return build();
    }
}
