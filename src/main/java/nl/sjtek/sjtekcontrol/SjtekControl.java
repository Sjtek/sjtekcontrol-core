package nl.sjtek.sjtekcontrol;

import com.sun.net.httpserver.HttpServer;
import nl.sjtek.sjtekcontrol.handlers.ApiHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class SjtekControl {

    private ApiHandler apiHandler;

    public SjtekControl() {
        System.out.println("Starting SjtekControl");
        System.out.println("Starting SjtekAPI");
        this.apiHandler = ApiHandler.getInstance();
    }

    public static void main(String args[]) throws IOException {
        new SjtekControl().start();
    }

    public void start() throws IOException {
        System.out.print("Starting server... ");
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext(ApiHandler.CONTEXT, apiHandler);
        server.setExecutor(null);
        System.out.println("on port 8000");
        server.start();
    }
}

