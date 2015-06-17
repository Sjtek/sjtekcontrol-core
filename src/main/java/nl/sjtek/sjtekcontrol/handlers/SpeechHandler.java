package nl.sjtek.sjtekcontrol.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import nl.sjtek.sjtekcontrol.data.Response;
import nl.sjtek.sjtekcontrol.utils.Speech;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by wouter on 16-6-15.
 */
public class SpeechHandler implements HttpHandler {

    public static final String CONTEXT = "/voice";

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String voiceString = "";
        try {
            voiceString = httpExchange.getRequestURI().getQuery().split("=")[1];
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println();
        System.out.println(httpExchange.getRemoteAddress().toString() + " | " +
                httpExchange.getRequestURI().getPath() + " | Voice: " + voiceString);
        System.out.print("    ");
        Speech.speechAsync(voiceString);
        String response = "{ }";
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
        System.out.println();
    }
}
