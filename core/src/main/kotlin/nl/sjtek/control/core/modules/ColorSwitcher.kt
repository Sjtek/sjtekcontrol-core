package nl.sjtek.control.core.modules

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import net.engio.mbassy.listener.Handler
import nl.sjtek.control.core.Color
import nl.sjtek.control.core.events.Bus
import nl.sjtek.control.core.events.ModuleUpdate
import nl.sjtek.control.core.events.SwitchEvent
import nl.sjtek.control.core.get
import nl.sjtek.control.data.response.Music
import nl.sjtek.control.data.response.Response
import nl.sjtek.control.data.staticdata.User
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit
import nl.sjtek.control.data.response.Color as ColorResponse

class ColorSwitcher(key: String) : Module(key) {
    private val logger = LoggerFactory.getLogger(javaClass)
    override val response: Response
        get() = ColorResponse(key, enabledLamps)
    private val enabledLamps: MutableSet<Int> = mutableSetOf()
    private var emitter: FlowableEmitter<Color>? = null

    init {
        Flowable.create({ e: FlowableEmitter<Color> ->
            emitter = e
        }, BackpressureStrategy.LATEST)
                .debounce(250, TimeUnit.MILLISECONDS)
                .subscribe { c ->
                    applyColor(c)
                }
        Bus.subscribe(this)
    }

    override fun isEnabled(user: User?): Boolean {
        return super.isEnabled(user)
    }

    override fun initSpark() {
        spark.Spark.path("/color") {
            get("/disable", this::disable)
            get("/enable", this::enable)
        }
    }

    @Handler
    fun onModuleUpdate(event: ModuleUpdate) {
        val res = event.response
        if (res is Music) {
            emitter?.onNext(Color(res.red, res.green, res.blue))
        }
    }

    private fun enable(req: spark.Request, res: spark.Response) {
        val query = req.queryMap()
        if (query.hasKey("lamps")) {
            query["lamps"].value().split(",").forEach {
                val id = it.toIntOrNull()
                if (id != null) enabledLamps.add(id)
            }
        }
        logger.info("${enabledLamps.size} lamps enabled")
    }

    private fun disable(req: spark.Request, res: spark.Response) {
        applyColor(255, 160, 10)
        enabledLamps.clear()
        logger.info("Lamps disabled")
    }

    private fun applyColor(c: Color) = applyColor(c.r, c.g, c.b)
    private fun applyColor(r: Int, g: Int, b: Int) {
        if (enabledLamps.isEmpty() || (r == 0 && g == 0 && b == 0)) return
        logger.info("Color r$r g$g b$b")
        enabledLamps.forEach {
            Bus.post(SwitchEvent(it, true, r, g, b))
        }
    }
}