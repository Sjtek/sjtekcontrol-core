package nl.sjtek.control.core.net

import io.habets.mopidy.base.net.client.MopidySocket
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class MopidyWebSocket(private val url: String) : MopidySocket() {

    private var webSocket: WebSocket? = null

    override fun connect() {
        webSocket?.close(1000, "")
        val request = Request.Builder()
                .url(url)
                .build()
        webSocket = HttpClient.client.newWebSocket(request, Listener())
    }

    override fun disconnect() {
        webSocket?.close(1000, "")
    }

    override fun sendData(data: String?) {
        webSocket?.send(data)
    }

    inner class Listener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket?, response: Response?) {
            this@MopidyWebSocket.onConnected()
        }

        override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
            this@MopidyWebSocket.onError(Exception(t))
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
            this@MopidyWebSocket.onDataReceived(text)
        }

        override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
            this@MopidyWebSocket.onDisconnected(false)
        }
    }
}