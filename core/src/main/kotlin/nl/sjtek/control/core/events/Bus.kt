package nl.sjtek.control.core.events

import net.engio.mbassy.bus.MBassador
import org.slf4j.LoggerFactory

object Bus {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val bus = MBassador<Any>()

    fun post(event: Event) {
        bus.publish(event)
    }

    fun subscribe(subscriber: Any) {
        bus.subscribe(subscriber)
    }

    fun unsubscribe(subscriber: Any) {
        bus.unsubscribe(subscriber)
    }
}