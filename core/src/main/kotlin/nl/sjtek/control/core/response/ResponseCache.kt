package nl.sjtek.control.core.response

import com.google.gson.GsonBuilder
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import nl.sjtek.control.core.events.BroadcastEvent
import nl.sjtek.control.core.events.Bus
import nl.sjtek.control.core.modules.Module
import nl.sjtek.control.data.response.Response
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit


object ResponseCache {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val responses: MutableMap<String, Response> = mutableMapOf()
    var json: String = "{}"
        private set
    private var previousJson: String = "{}"
    private var emitter: FlowableEmitter<Module>? = null

    init {
        Flowable.create({ e: FlowableEmitter<Module> ->
            emitter = e
        }, BackpressureStrategy.LATEST)
                .debounce(750, TimeUnit.MILLISECONDS)
                .subscribe { module ->
                    if (json != previousJson) {
                        previousJson = json
                        logger.info("Broadcasting update (${module.key})")
                        Bus.post(BroadcastEvent(json))
                    }
                }
    }

    fun getKeys(keys: List<String>): String {
        val result = responses.filter { it.key in keys }
        return gson.toJson(result)
    }

    @Synchronized
    fun post(module: Module, broadcast: Boolean = false) {
        responses.put(module.key, module.response)
        json = gson.toJson(responses)

        if (broadcast) emitter?.onNext(module)
    }
}