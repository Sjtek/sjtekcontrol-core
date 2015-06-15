package nl.sjtek.sjtekcontrol.utils;

import com.sun.net.httpserver.HttpExchange;

/**
 * Created by wouter on 11-6-15.
 */
public class Utils {

    public static boolean isDoVoice(HttpExchange httpExchange) {
        return httpExchange.getRequestURI().getQuery().contains("voice=true");
    }
}
