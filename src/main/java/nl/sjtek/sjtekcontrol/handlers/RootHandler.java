package nl.sjtek.sjtekcontrol.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import nl.sjtek.sjtekcontrol.data.Response;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

public class RootHandler implements HttpHandler {

    public static final String CONTEXT = "/api";

    private Response execute(String path[], boolean useVoice) {
        return new Response(200);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        LocalDateTime dateTime = LocalDateTime.now();
        System.out.print(dateTime.toString() + " ");
        System.out.println(httpExchange.getRemoteAddress().toString());
        System.out.print(httpExchange.getRequestURI().getPath());
        System.out.println("?" + httpExchange.getRequestURI().getQuery());

        String path[] = httpExchange.getRequestURI().getPath().split("/");
        boolean useVoice = httpExchange.getRequestURI().getQuery().contains("voice");

        Response response = execute(path, useVoice);

        httpExchange.sendResponseHeaders(response.getCode(), response.toString().length());
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(response.toString().getBytes());
        outputStream.close();
    }
}
