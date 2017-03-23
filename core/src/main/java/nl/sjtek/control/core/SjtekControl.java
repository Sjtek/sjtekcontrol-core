package nl.sjtek.control.core;

import com.sun.net.httpserver.HttpServer;
import io.habets.javautils.Log;
import nl.sjtek.control.core.network.ApiHandler;
import nl.sjtek.control.core.settings.SettingsManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class SjtekControl {

    private ApiHandler apiHandler;

    public SjtekControl() {
        long start = System.currentTimeMillis();
        SettingsManager.getInstance().reload();
        this.apiHandler = ApiHandler.getInstance();
        long end = System.currentTimeMillis();
        Log.i("SjtekControl", "Loaded in " + (end - start) + "ms");
    }

    public static void main(String args[]) throws IOException {
        Log.setLevel(Log.Level.DEBUG);
        Log.setListener(new Log.PrintListener(false));
        new SjtekControl().start();
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext(ApiHandler.CONTEXT, apiHandler);
        server.setExecutor(null);
        Log.i("SjtekControl", "Starting server on port 8000");
        server.start();
    }
}

