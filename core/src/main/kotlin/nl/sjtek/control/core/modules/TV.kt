package nl.sjtek.control.core.modules

import nl.sjtek.control.core.Executor
import nl.sjtek.control.core.events.AudioEvent
import nl.sjtek.control.core.events.Bus
import nl.sjtek.control.core.response.ResponseCache
import nl.sjtek.control.core.settings.Settings
import nl.sjtek.control.data.response.Response
import nl.sjtek.control.data.response.TV
import org.slf4j.LoggerFactory
import java.io.IOException

class TV(key: String, settings: Settings) : Module(key, settings) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private var enabled: Boolean = false
        set(value) {
            field = value
            logger.info("TV $value")
            Bus.post(AudioEvent(key, value))
            ResponseCache.post(this, true)
        }
    override val response: Response
        get() = TV(enabled)

    init {
        PingThread(settings.tv.ip).start()
    }

    override fun initSpark() {

    }

    inner class PingThread(private val ip: String) : Thread() {
        override fun run() {
            super.run()
            while (true) {
                try {
                    Thread.sleep(2000)
                    val result = Executor.execute(getCommand())
                    if (result == 0) {
                        if (!enabled) {
                            enabled = true
                        }
                    } else {
                        if (enabled) {
                            enabled = false
                        }
                    }
                } catch (e: IOException) {
                    logger.error("TV ping error", e)
                } catch (e: InterruptedException) {
                    logger.error("TV ping error", e)
                }
            }
        }

        private fun getCommand(): Array<String> = arrayOf("/bin/ping", "-c 1", ip)
    }
}
