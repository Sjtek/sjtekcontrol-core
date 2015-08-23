package nl.sjtek.sjtekcontrol.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import nl.sjtek.sjtekcontrol.data.Arguments;
import nl.sjtek.sjtekcontrol.utils.Page;
import nl.sjtek.sjtekcontrol.utils.Speech;

import java.io.IOException;
import java.io.OutputStream;

public class SpeechHandler implements HttpHandler {

    public static final String CONTEXT = "/speech";

    public SpeechHandler() {

    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Arguments arguments = new Arguments(httpExchange.getRequestURI().getQuery());
        String fullPath = httpExchange.getRequestURI().getPath().toLowerCase();
        System.out.println();
        System.out.println(httpExchange.getRemoteAddress().toString() + " | " +
                fullPath + " | " + arguments.toString());

        int responseCode = 200;
        String response = "{ }";

        if (fullPath.equals(CONTEXT) && arguments.getText() != null && !arguments.getText().isEmpty()) {
            Speech.speek(arguments.getText());
        } else {
            responseCode = 404;
            response = Page.getPage(responseCode);
        }


        httpExchange.sendResponseHeaders(responseCode, response.getBytes().length);
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
    }
}
