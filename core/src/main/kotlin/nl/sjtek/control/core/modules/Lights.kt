package nl.sjtek.control.core.modules

import net.engio.mbassy.listener.Handler
import nl.sjtek.control.core.*
import nl.sjtek.control.core.events.Bus
import nl.sjtek.control.core.events.MotionSensorEvent
import nl.sjtek.control.core.events.SwitchStateEvent
import nl.sjtek.control.core.events.ToggleEvent
import nl.sjtek.control.core.response.ResponseCache
import nl.sjtek.control.core.settings.SettingsManager
import nl.sjtek.control.data.response.Lights
import nl.sjtek.control.data.response.Response
import nl.sjtek.control.data.static.Lamp
import nl.sjtek.control.data.static.User
import org.slf4j.LoggerFactory
import spark.Spark.halt
import spark.Spark.path
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.collections.set


class Lights(key: String) : Module(key) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val executor: ScheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(1)
    private val lamps: Map<Int, Lamp> = SettingsManager.lamps
    private val schedule: MutableMap<String, ScheduledFuture<*>> = mutableMapOf()
    override val response: Response
        get() = Lights(key, lamps.entries.associate { e -> Pair(e.key, e.value.state) })

    init {
        Bus.subscribe(this)
    }

    override fun initSpark() {
        path("/lights") {
            get("/:lamp/toggle", this::lampToggle)
            get("/:lamp/on", this::lampOn)
            get("/:lamp/off", this::lampOff)
            spark.Spark.get("/:lamp/status", this::lampStatus)
            spark.Spark.get("/:lamp/color", this::lampColor)
        }
        path("/room") {
            get("/:room/toggle", this::roomToggle)
            get("/:room/on", this::roomOn)
            get("/:room/off", this::roomOff)
        }
    }

    @Handler
    fun onStateChange(event: SwitchStateEvent) {
        val lamp = lamps.values.find { it.internalId == event.id } ?: return
        lamp.state = event.state
        ResponseCache.post(this, true)
    }

    @Handler
    fun onToggle(event: ToggleEvent) {
        lamps.values.forEach {
            if (it.owner == null || it.owner == event.user?.username) {
                if (event.enabled) {
                    if (it.onlyOff) {

                    } else if (it.hasSensor()) {
                        it.motion()
                    } else {
                        it.turnOn()
                    }
                } else {
                    if (!it.hasSensor()) it.turnOff()
                }
            }
        }
    }

    @Handler
    fun onSensorEvent(event: MotionSensorEvent) {
        val lamps = lamps.values.filter { it.sensorId == event.id }
        logger.info("Sensor ${event.id} detected motion, ${lamps.size} paired")
        lamps.motion()
    }

    override fun isEnabled(user: User?): Boolean {
        lamps.values.forEach {
            if (it.owner == null && !it.hasSensor()) {
                if (it.state) return true
            } else {
                if (it.owner == user?.username && !it.hasSensor()) {
                    if (it.state) return true
                }
            }
        }
        return false
    }

    private fun lampToggle(req: spark.Request, res: spark.Response) {
        val lamp: Lamp = getLamp(req) ?: throw halt(404, "Lamp not found")
        lamp.toggle(Color.parseQuery(req.queryMap()))
    }

    private fun lampOn(req: spark.Request, res: spark.Response) {
        val lamp = getLamp(req) ?: throw halt(404, "Lamp not found")
        lamp.turnOn(Color.parseQuery(req.queryMap()))
    }

    private fun lampOff(req: spark.Request, res: spark.Response) {
        val lamp = getLamp(req) ?: throw halt(404, "Lamp not found")
        lamp.turnOff()
    }

    private fun roomToggle(req: spark.Request, res: spark.Response) {
        val room = getRoom(req)
        if (room.isEmpty()) throw halt(404, "Room not found")
        room.toggle(Color.parseQuery(req.queryMap()))
    }

    private fun roomOn(req: spark.Request, res: spark.Response) {
        val room = getRoom(req)
        if (room.isEmpty()) throw halt(404, "Room not found")
        room.turnOn(Color.parseQuery(req.queryMap()))
    }

    private fun roomOff(req: spark.Request, res: spark.Response) {
        val room = getRoom(req)
        if (room.isEmpty()) throw halt(404, "Room not found")
        room.turnOff()
    }

    private fun lampStatus(req: spark.Request, res: spark.Response): String {
        val lamp = getLamp(req) ?: throw halt(404, "Lamp not found")
        return if (lamp.state) "1" else "0"
    }

    private fun lampColor(req: spark.Request, res: spark.Response): String = "000000"

    private fun getLamp(req: spark.Request): Lamp? {
        val input = req.params(":lamp")
        return try {
            val id = input.toInt()
            lamps[id]
        } catch (e: NumberFormatException) {
            lamps.values.find {
                it.name == input
            }
        }
    }

    private fun getRoom(req: spark.Request): List<Lamp> {
        val input = req.params(":room")
        return lamps.values.filter { it.room == input }
    }

    private fun List<Lamp>.turnOn(color: Color = Color()) = this.forEach { it.turnOn(color) }
    private fun List<Lamp>.turnOff() = this.forEach { it.turnOff() }
    private fun List<Lamp>.toggle(color: Color = Color()) {
        var isOn = false
        this.forEach {
            if (it.state) isOn = true
        }
        if (isOn) this.turnOff() else turnOn(color)
    }

    private fun List<Lamp>.motion() = this.forEach { it.motion() }
    private fun Lamp.motion() {
        schedule[this.name]?.cancel(false)
        schedule[this.name] = executor.schedule(this::turnOff, 2, TimeUnit.MINUTES)
        this.turnOn()
    }
}