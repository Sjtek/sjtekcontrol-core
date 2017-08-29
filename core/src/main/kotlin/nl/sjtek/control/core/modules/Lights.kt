package nl.sjtek.control.core.modules

import net.engio.mbassy.listener.Handler
import nl.sjtek.control.core.events.*
import nl.sjtek.control.core.get
import nl.sjtek.control.core.response.ResponseCache
import nl.sjtek.control.core.settings.SettingsManager
import nl.sjtek.control.core.settings.User
import nl.sjtek.control.data.response.Lights
import nl.sjtek.control.data.response.Response
import spark.QueryParamsMap
import spark.Spark.halt
import spark.Spark.path
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.collections.set


class Lights(key: String) : Module(key) {
    private val executor: ScheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(1)
    private val lamps: Map<Int, Lamp> = mapOf(
            1 to Lamp("livingroom", 1, true, "livingroom"),
            2 to Lamp("couch", 2, false, "livingroom"),
            3 to Lamp("kitchen", 3, false, "livingroom"),
            4 to Lamp("hallway", 4, true, "hallway", sensorId = 1),
            5 to Lamp("dishwasher", 5, true, "hallway", sensorId = 1),
            6 to Lamp("stairs", 6, true, "stairs", sensorId = 2, onlyOff = true),
            7 to Lamp("wouters desk light", 7, true, "wouter", owner = SettingsManager.getUser("wouter")),
            8 to Lamp("wouters led strip", 8, true, "wouter", owner = SettingsManager.getUser("wouter")),
            9 to Lamp("tijns room", 9, true, "tijn", owner = SettingsManager.getUser("tijn")))
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
        val lamp = lamps.values.find { it.id == event.id } ?: return
        lamp.state = event.state
        ResponseCache.post(this, true)
    }

    @Handler
    fun onToggle(event: ToggleEvent) {
        lamps.values.forEach {
            if (it.owner == null || it.owner == event.user) {
                if (event.enabled) {
                    if (!it.onlyOff) it.turnOn()
                } else it.turnOff()
            }
        }
    }

    fun onSensorEvent(event: MotionSensorEvent) {
        val lamps = lamps.values.filter { it.sensorId == event.id }

    }

    override fun isEnabled(user: User?): Boolean {
        lamps.values.forEach {
            if (it.owner == null) {
                if (it.state) return true
            } else {
                if (it.owner == user) {
                    if (it.state) return true
                }
            }
        }
        return false
    }

    private fun lampToggle(req: spark.Request, res: spark.Response) {
        val lamp: Lamp = getLamp(req) ?: throw halt(404, "Lamp not found")
        lamp.toggle(Color.parseQuery(req.queryMap()))
        ResponseCache.post(this, true)
    }

    private fun lampOn(req: spark.Request, res: spark.Response) {
        val lamp = getLamp(req) ?: throw halt(404, "Lamp not found")
        lamp.turnOn(Color.parseQuery(req.queryMap()))
        ResponseCache.post(this, true)
    }

    private fun lampOff(req: spark.Request, res: spark.Response) {
        val lamp = getLamp(req) ?: throw halt(404, "Lamp not found")
        lamp.turnOff()
        ResponseCache.post(this, true)
    }

    private fun roomToggle(req: spark.Request, res: spark.Response) {
        val room = getRoom(req)
        if (room.isEmpty()) throw halt(404, "Room not found")
        room.toggle(Color.parseQuery(req.queryMap()))
        ResponseCache.post(this, true)
    }

    private fun roomOn(req: spark.Request, res: spark.Response) {
        val room = getRoom(req)
        if (room.isEmpty()) throw halt(404, "Room not found")
        room.turnOn(Color.parseQuery(req.queryMap()))
        ResponseCache.post(this, true)
    }

    private fun roomOff(req: spark.Request, res: spark.Response) {
        val room = getRoom(req)
        if (room.isEmpty()) throw halt(404, "Room not found")
        room.turnOff()
        ResponseCache.post(this, true)
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

    private data class Color(val r: Int = -1, val g: Int = -1, val b: Int = -1) {
        companion object {
            fun parseQuery(query: QueryParamsMap): Color {
                if (query.hasKey("hex")) {
                    val hexValue = query["hex"].value()
                    return try {
                        val r = hexValue.substring(0, 2).toInt(radix = 16)
                        val g = hexValue.substring(2, 4).toInt(radix = 16)
                        val b = hexValue.substring(4, 6).toInt(radix = 16)
                        Color(r, g, b)
                    } catch (e: Exception) {
                        Color()
                    }

                } else {
                    val sR = query["r"].integerValue() ?: return Color()
                    val sG = query["g"].integerValue() ?: return Color()
                    val sB = query["b"].integerValue() ?: return Color()
                    return Color(sR, sG, sB)
                }
            }
        }
    }

    private data class Lamp(val name: String, val id: Int, val rgb: Boolean, val room: String, var state: Boolean = false, val sensorId: Int = -1, val owner: User? = null, val onlyOff: Boolean = false) {

        fun turnOn(color: Color) = turnOn(color.r, color.g, color.b)
        fun turnOn(red: Int = -1, green: Int = -1, blue: Int = -1) {
            Bus.post(SwitchEvent(id, true, red, green, blue))
        }

        fun turnOff() {
            Bus.post(SwitchEvent(id, false))
        }

        fun toggle(color: Color = Color()) = if (state) turnOff() else turnOn(color)
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

    private fun List<Lamp>.motion() {
        this.forEach {
            schedule[it.name]?.cancel(false)
            schedule[it.name] = executor.schedule(it::turnOff, 10, TimeUnit.MINUTES)
            it.turnOn()
        }
    }
}