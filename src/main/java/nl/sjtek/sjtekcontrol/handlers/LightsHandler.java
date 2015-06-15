package nl.sjtek.sjtekcontrol.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class LightsHandler implements HttpHandler {

    public static final String CONTEXT = "/api/lights";

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

    }
}
