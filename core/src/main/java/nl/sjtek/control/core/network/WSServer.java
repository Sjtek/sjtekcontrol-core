package nl.sjtek.control.core.network;

import com.google.common.eventbus.Subscribe;
import nl.sjtek.control.core.events.BroadcastEvent;
import nl.sjtek.control.core.events.Bus;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;


public class WSServer extends WebSocketServer {

    private static final int PORT = 8001;

    public WSServer() {
        super(new InetSocketAddress(PORT));
        Bus.regsiter(this);
    }

    private synchronized void sendUpdate(String data) {
        for (WebSocket webSocket : connections()) {
            webSocket.send(data);
        }
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

    }

    @Override
    public void onMessage(WebSocket conn, String message) {

    }

    @Override
    public void onError(WebSocket conn, Exception ex) {

    }

    @Subscribe
    public void onBroadcast(BroadcastEvent event) {
        new DataSender(event.getData()).start();
    }

    private class DataSender extends Thread {

        private final String data;

        private DataSender(String data) {
            this.data = data;
        }

        @Override
        public void run() {
            sendUpdate(data);
        }
    }
}
