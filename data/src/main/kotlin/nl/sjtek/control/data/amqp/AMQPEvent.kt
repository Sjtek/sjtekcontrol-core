package nl.sjtek.control.data.amqp

abstract class AMQPEvent {
    abstract fun toMessage(): ByteArray
}