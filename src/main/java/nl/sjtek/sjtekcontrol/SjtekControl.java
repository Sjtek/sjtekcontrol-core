package nl.sjtek.sjtekcontrol;

import com.sun.net.httpserver.HttpServer;
import nl.sjtek.sjtekcontrol.handlers.ApiHandler;
import nl.sjtek.sjtekcontrol.handlers.RootHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class SjtekControl {

    private RootHandler rootHandler;
    private ApiHandler apiHandler;

    public SjtekControl() {
        System.out.println("Starting SjtekControl.");
        this.rootHandler = new RootHandler();
        this.apiHandler = new ApiHandler();
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext(ApiHandler.CONTEXT, apiHandler);
        server.createContext(RootHandler.CONTEXT, rootHandler);
        server.setExecutor(null);
        System.out.println("Listening on port 8000.");
        server.start();
    }

    public static void main(String args[]) throws IOException {
        new SjtekControl().start();
    }


}

