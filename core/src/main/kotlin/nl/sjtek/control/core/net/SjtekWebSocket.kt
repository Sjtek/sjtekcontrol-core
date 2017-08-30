package nl.sjtek.control.core.net

import net.engio.mbassy.listener.Handler
import nl.sjtek.control.core.events.BroadcastEvent
import nl.sjtek.control.core.events.Bus
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

@WebSocket
class SjtekWebSocket {

    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        Bus.subscribe(this)
    }

    @OnWebSocketConnect
    fun connected(session: Session) {
        sessions.add(session)
    }

    @OnWebSocketClose
    fun disconnected(session: Session, statusCode: Int, reason: String?) {
        sessions.remove(session)
    }

    @Handler
    fun onBroadcast(event: BroadcastEvent) {
        sessions.forEach {
            if (it.isOpen) {
                it.remote.sendString(event.json)
            } else {
                logger.warn("WebSocket not properly removed from queue: ${it.remoteAddress}")
            }
        }
    }

    companion object {
        private val sessions: Queue<Session> = ConcurrentLinkedQueue()
    }
}