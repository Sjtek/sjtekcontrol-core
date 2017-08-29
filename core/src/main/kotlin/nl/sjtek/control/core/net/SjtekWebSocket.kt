package nl.sjtek.control.core.net

import net.engio.mbassy.listener.Handler
import nl.sjtek.control.core.events.BroadcastEvent
import nl.sjtek.control.core.events.Bus
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

@WebSocket
class SjtekWebSocket {

    init {
        Bus.subscribe(this)
    }

    @OnWebSocketConnect
    fun connected(session: Session) {
        sessions.add(session)
    }

    @OnWebSocketClose
    fun disconnected(session: Session, statusCode: Int, reason: String?) {
        sessions.add(session)
    }

    @Handler
    fun onBroadcast(event: BroadcastEvent) {
        sessions.forEach { it.remote.sendString(event.json) }
    }

    companion object {
        private val sessions: Queue<Session> = ConcurrentLinkedQueue()
    }
}