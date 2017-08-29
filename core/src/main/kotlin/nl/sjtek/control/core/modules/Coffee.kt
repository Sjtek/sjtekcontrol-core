package nl.sjtek.control.core.modules

import nl.sjtek.control.core.events.Bus
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

    override fun initSpark() {
        path("/coffee") {
            get("/enable", this::enable)
        }
    }

    private fun enable(req: spark.Request, res: spark.Response) {
        TODO("Fix me")
        if (!enabled) {
            lastTimeEnabled = System.currentTimeMillis()
            Bus.post(SwitchEvent(10, false))
            Timer().schedule(2000) {
                Bus.post(SwitchEvent(10, true))
            }
        }
    }
}
