package nl.sjtek.control.core.modules

import nl.sjtek.control.core.events.Bus
import nl.sjtek.control.core.events.NightModeEvent
import nl.sjtek.control.core.response.ResponseCache
import nl.sjtek.control.core.settings.Settings
import nl.sjtek.control.data.response.NightMode
import nl.sjtek.control.data.response.Response
import spark.Request
import spark.Spark.get
import spark.Spark.path
import spark.Response as SparkResponse

class NightMode(key: String, settings: Settings) : Module(key, settings) {

    override val response: Response
        get() = NightMode(enabled)
    private var enabled: Boolean = false
        set(value) {
            field = value
            Bus.post(NightModeEvent(value))
            ResponseCache.post(this)
        }

    override fun initSpark() {
        path("/nightmode") {
            get("/enable", this::enable)
            get("/disable", this::disable)
            get("/toggle", this::toggle)
        }
    }

    private fun enable(req: Request, res: SparkResponse) {
        enabled = true
    }

    private fun disable(req: Request, res: SparkResponse) {
        enabled = false
    }

    private fun toggle(req: Request, res: SparkResponse) {
        enabled = !enabled
    }
}