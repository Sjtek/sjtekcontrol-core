package nl.sjtek.sjtekcontrol;

import com.sun.net.httpserver.HttpServer;
import nl.sjtek.sjtekcontrol.handlers.ApiHandler;
import nl.sjtek.sjtekcontrol.handlers.RootHandler;
import nl.sjtek.sjtekcontrol.handlers.SpeechHandler;
import nl.sjtek.sjtekcontrol.utils.Speech;

import java.io.IOException;
import java.net.InetSocketAddress;

public class SjtekControl {

    private RootHandler rootHandler;
    private ApiHandler apiHandler;
    private SpeechHandler speechHandler;

    public SjtekControl() {
        System.out.println("Starting SjtekControl");
        System.out.println("Starting SjtekServe");
        this.rootHandler = new RootHandler();
        System.out.println("Starting SjtekAPI");
        this.apiHandler = new ApiHandler();
        System.out.println("Starting SpeechAPI");
        this.speechHandler = new SpeechHandler();
    }

    public void start() throws IOException {
        System.out.print("Starting server... ");
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext(ApiHandler.CONTEXT, apiHandler);
        server.createContext(RootHandler.CONTEXT, rootHandler);
        server.createContext(SpeechHandler.CONTEXT, speechHandler);
        server.setExecutor(null);
        System.out.println("on port 8000");
        server.start();
    }

    public static void main(String args[]) throws IOException {
        new SjtekControl().start();
    }


}

