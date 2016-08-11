package nl.sjtek.control.core.network;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class WSServer extends WebSocketServer implements BroadcastListener {

    private static final int PORT = 8001;

    public WSServer() {
        super(new InetSocketAddress(PORT));
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

    @Override
    public void onBroadcast(String data) {
        new DataSender(data).start();
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
