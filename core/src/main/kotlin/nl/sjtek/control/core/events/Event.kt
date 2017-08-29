package nl.sjtek.control.core.events

import nl.sjtek.control.data.amqp.SensorEvent
import nl.sjtek.control.data.amqp.SwitchEvent as AMQPSwitch
import nl.sjtek.control.data.amqp.SwitchStateEvent as AMQPSwitchState

abstract class Event
data class BroadcastEvent(val json: String) : Event()
data class ToggleEvent(val enabled: Boolean) : Event()
data class NightModeEvent(val enabled: Boolean) : Event()
data class AudioEvent(val key: String, val state: Boolean) : Event()
data class SwitchEvent(val id: Int, val state: Boolean, val red: Int = -1, val green: Int = -1, val blue: Int = -1) : Event() {
    val hasColor: Boolean
        get() = (red < 0 || green < 0 || blue < 0)

    fun toAMQP(): AMQPSwitch = AMQPSwitch(id, state, red, green, blue)
}

fun AMQPSwitch.toInternalEvent(): SwitchEvent = SwitchEvent(this.id, this.state, this.r, this.g, this.b)

data class SwitchStateEvent(val id: Int, val state: Boolean) : Event() {
    fun toAMQP(): AMQPSwitchState = AMQPSwitchState(id, state)
}

fun AMQPSwitchState.toInternalEvent(): SwitchStateEvent = SwitchStateEvent(this.id, this.state)

data class MotionSensorEvent(val id: Int, val state: Boolean) : Event() {
    fun toAMQP(): SensorEvent = SensorEvent(SensorEvent.Type.MOTION, id, if (state) 1f else 0f)
}

data class TemperatureEvent(val id: Int, val temperature: Float, val humidity: Float) : Event() {
    fun toAMQP(): SensorEvent = SensorEvent(SensorEvent.Type.TEMPERATURE, id, temperature, humidity)
}

data class LightSensorEvent(val id: Int, val value: Float) : Event() {
    fun toAMQP(): SensorEvent = SensorEvent(SensorEvent.Type.LIGHT, id, value)
}

fun SensorEvent.toInternalEvent(): Event = when (this.type) {
    SensorEvent.Type.MOTION -> MotionSensorEvent(this.id, this.value1 == 1f)
    SensorEvent.Type.TEMPERATURE -> TemperatureEvent(this.id, this.value1, this.value2)
    SensorEvent.Type.LIGHT -> LightSensorEvent(this.id, this.value1)
}
