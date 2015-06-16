package nl.sjtek.sjtekcontrol.handlers;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import nl.sjtek.sjtekcontrol.data.Response;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by wouter on 15-6-15.
 */
public class SjtekHandler implements HttpHandler {

    public Response execute(String[] path, boolean useVoice) {
        return new Response(404);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path[] = httpExchange.getRequestURI().getPath().split("/");
        String query = httpExchange.getRequestURI().getQuery();
        boolean useVoice = query != null && query.contains("voice");
        System.out.println();
        System.out.println(httpExchange.getRemoteAddress().toString() + " | " +
                httpExchange.getRequestURI().getPath() + " | Voice: " + useVoice);
        System.out.print("    ");
        Response response = execute(path, useVoice);
        System.out.println(response.toString());
        httpExchange.sendResponseHeaders(response.getCode(), response.toString().length());
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(response.toString().getBytes());
        outputStream.close();
        System.out.println();
    }
}
