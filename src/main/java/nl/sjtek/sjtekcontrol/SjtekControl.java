package nl.sjtek.sjtekcontrol;

import com.sun.net.httpserver.HttpServer;
import nl.sjtek.sjtekcontrol.handlers.LightsHandler;
import nl.sjtek.sjtekcontrol.handlers.MusicHandler;
import nl.sjtek.sjtekcontrol.handlers.RootHandler;
import nl.sjtek.sjtekcontrol.handlers.SpeechHandler;
import org.bff.javampd.exception.MPDConnectionException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class SjtekControl {

    private MusicHandler musicHandler;
    private LightsHandler lightsHandler;
    private SpeechHandler speechHandler;
    private RootHandler rootHandler;

    public SjtekControl() throws UnknownHostException, MPDConnectionException {
        System.out.println("Starting SjtekControl.");
        musicHandler = new MusicHandler();
        lightsHandler = new LightsHandler();
        speechHandler = new SpeechHandler();
        rootHandler = new RootHandler();
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext(MusicHandler.CONTEXT, musicHandler);
        server.createContext(LightsHandler.CONTEXT, lightsHandler);
        server.createContext(SpeechHandler.CONTEXT, speechHandler);
        server.createContext(RootHandler.CONTEXT, rootHandler);
        server.setExecutor(null);
        System.out.println("Listening on port 8000.");
        server.start();
    }

    public static void main(String args[]) throws IOException {
        try {
            new SjtekControl().start();
        } catch (MPDConnectionException e) {
            e.printStackTrace();
        }
    }
}

