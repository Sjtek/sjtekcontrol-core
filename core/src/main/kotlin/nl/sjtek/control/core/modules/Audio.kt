package nl.sjtek.control.core.modules

import net.engio.mbassy.listener.Handler
import nl.sjtek.control.core.events.AudioEvent
import nl.sjtek.control.core.events.Bus
import nl.sjtek.control.core.events.SwitchEvent
import nl.sjtek.control.core.response.ResponseCache
import nl.sjtek.control.data.response.Audio
import nl.sjtek.control.data.response.Response
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.concurrent.schedule

class Audio(key: String) : Module(key) {

    private val SWITCH_ID = 11

    private val logger = LoggerFactory.getLogger(javaClass)
    private val modules: MutableMap<String, Boolean> = mutableMapOf()
    private var timer: TimerTask? = null
    private val enabled: Boolean
        get() {
            modules.values.forEach { if (it) return true }
            return false
        }
    private var previousState: Boolean = false
    override val response: Response
        get() = Audio(key, enabled, modules)

    init {
        Bus.subscribe(this)
    }

    override fun initSpark() {

    }

    @Handler
    fun onAudioEvent(event: AudioEvent) {
        modules.put(event.key, event.state)
        ResponseCache.post(this, true)
        if (enabled) audioEnable() else audioDisable()
    }

    private fun audioEnable() {
        if (previousState) return
        previousState = true
        logger.info("Audio enabled")
        timer?.cancel()
        Bus.post(SwitchEvent(SWITCH_ID, true))
    }

    private fun audioDisable() {
        if (!previousState) return
        previousState = false
        logger.info("Scheduling disable")
        timer?.cancel()
        timer = Timer().schedule(30000) {
            Bus.post(SwitchEvent(SWITCH_ID, false))
            logger.info("Audio disabled")
        }
    }
}