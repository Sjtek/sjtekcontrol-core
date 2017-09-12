package nl.sjtek.control.core.events

import net.engio.mbassy.bus.MBassador
import net.engio.mbassy.bus.config.BusConfiguration
import net.engio.mbassy.bus.config.Feature
import net.engio.mbassy.bus.error.PublicationError
import org.slf4j.LoggerFactory

object Bus {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val bus: MBassador<Event>

    init {
        val config = BusConfiguration()
                .addPublicationErrorHandler(this::errorHandler)
                .addFeature(Feature.SyncPubSub.Default())
                .addFeature(Feature.AsynchronousHandlerInvocation.Default())
                .addFeature(Feature.AsynchronousMessageDispatch.Default())
        bus = MBassador(config)
    }

    fun post(event: Event) {
        bus.publish(event)
    }

    fun subscribe(subscriber: Any) {
        bus.subscribe(subscriber)
    }

    fun unsubscribe(subscriber: Any) {
        bus.unsubscribe(subscriber)
    }

    private fun errorHandler(error: PublicationError) {
        logger.error("Bus error", error)
        error.cause.printStackTrace()
    }
}