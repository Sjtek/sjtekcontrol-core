package nl.sjtek.control.core.modules

import net.engio.mbassy.listener.Handler
import nl.sjtek.control.core.events.Bus
import nl.sjtek.control.core.events.CoffeeEvent
import nl.sjtek.control.core.events.SwitchEvent
import nl.sjtek.control.core.get
import nl.sjtek.control.data.response.Coffee
import nl.sjtek.control.data.response.Response
import spark.Spark.path
import java.util.*
import kotlin.concurrent.schedule

class Coffee(key: String) : Module(key) {
    private val TIMEOUT: Long = 600000
    private val enabled: Boolean
        get() = System.currentTimeMillis() - lastTimeEnabled < TIMEOUT
    private var lastTimeEnabled: Long = 0
    override val response: Response
        get() = Coffee(key, enabled, lastTimeEnabled)

    init {
        Bus.subscribe(this)
    }

    override fun initSpark() {
        path("/coffee") {
            get("/enable", this::enable)
        }
    }

    @Handler
    fun onCoffeeEvent(event: CoffeeEvent) = enable()

    private fun enable(req: spark.Request? = null, res: spark.Response? = null) {
        if (!enabled) {
            lastTimeEnabled = System.currentTimeMillis()
            Bus.post(SwitchEvent(10, false))
            Timer().schedule(2000) {
                Bus.post(SwitchEvent(10, true))
            }
        }
    }
}
