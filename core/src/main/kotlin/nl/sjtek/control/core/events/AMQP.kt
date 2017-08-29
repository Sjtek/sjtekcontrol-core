package nl.sjtek.control.core.events

import com.rabbitmq.client.*
import com.rabbitmq.client.AMQP
import com.sun.istack.internal.logging.Logger
import net.engio.mbassy.listener.Handler
import nl.sjtek.control.core.settings.SettingsManager
import nl.sjtek.control.data.amqp.SensorEvent
import nl.sjtek.control.data.amqp.SwitchStateEvent
import nl.sjtek.control.data.amqp.SwitchEvent as AMQPSwitch

object AMQP {

    private val logger = Logger.getLogger(javaClass)

    private const val EXCHANGE_SWITCH = "lights"
    private const val EXCHANGE_STATES = "lights_state"
    private const val EXCHANGE_SENSORS = "sensors"

    private val channelSwitch: Channel
    private val channelSwitchState: Channel
    private val channelSensors: Channel

    init {
        val settings = SettingsManager.settings.amqp
        val factory = ConnectionFactory()
        factory.host = settings.host
        factory.username = settings.username
        factory.password = settings.password
        factory.isAutomaticRecoveryEnabled = true
        val connection = factory.newConnection()

        channelSwitch = createExchange(connection, EXCHANGE_SWITCH)

        channelSwitchState = createExchange(connection, EXCHANGE_STATES)
        addConsumer(EXCHANGE_STATES, ConsumerSwitchState(channelSwitchState))

        channelSensors = createExchange(connection, EXCHANGE_SENSORS)
        addConsumer(EXCHANGE_SENSORS, ConsumerSensors(channelSensors))

        logger.info("AMQP connected to ${settings.host}")
        Bus.subscribe(this)
    }

    fun init() {}

    @Handler
    fun onSwitch(event: SwitchEvent) {
        channelSwitch.basicPublish(EXCHANGE_SWITCH, "", null, event.toAMQP().toMessage())
    }

    private fun createExchange(connection: Connection, name: String, type: BuiltinExchangeType = BuiltinExchangeType.FANOUT): Channel {
        val channel = connection.createChannel()
        channel.exchangeDeclare(name, type)
        return channel
    }

    private fun addConsumer(exchange: String, consumer: DefaultConsumer) {
        val channel = consumer.channel
        val queueName = channel.queueDeclare().queue
        channel.queueBind(queueName, exchange, "")
        channel.basicConsume(queueName, true, consumer)
    }

    private class ConsumerSwitchState(channel: Channel) : DefaultConsumer(channel) {
        override fun handleDelivery(consumerTag: String?, envelope: Envelope?, properties: AMQP.BasicProperties?, body: ByteArray?) {
            if (body == null) return
            val data = String(body)
            val event = SwitchStateEvent.parseMessage(data) ?: return logger.warning("Invalid message $data")
            Bus.post(event.toInternalEvent())
        }
    }

    private class ConsumerSensors(channel: Channel) : DefaultConsumer(channel) {
        override fun handleDelivery(consumerTag: String?, envelope: Envelope?, properties: AMQP.BasicProperties?, body: ByteArray?) {
            if (body == null) return
            val data = String(body)
            val event = SensorEvent.fromMessage(data) ?: return logger.warning("Invalid message $data")
            Bus.post(event.toInternalEvent())
        }
    }
}