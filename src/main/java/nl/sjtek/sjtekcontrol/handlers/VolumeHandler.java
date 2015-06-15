package nl.sjtek.sjtekcontrol.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * Created by wouter on 11-6-15.
 */
public class VolumeHandler implements HttpHandler {

    public static final String CONTEXT = "/api/volume";

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

    }
}
