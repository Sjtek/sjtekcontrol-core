package nl.sjtek.control.core.modules

import nl.sjtek.control.core.ModuleManager
import nl.sjtek.control.core.events.Bus
import nl.sjtek.control.core.events.ToggleEvent
import nl.sjtek.control.core.get
import nl.sjtek.control.core.response.ResponseCache
import nl.sjtek.control.core.response.Transformer
import nl.sjtek.control.core.settings.SettingsManager
import spark.Request
import spark.Response
import nl.sjtek.control.data.response.Base as BaseResponse

class Base(key: String) : Module(key) {

    override val response: BaseResponse
        get() = BaseResponse(key)

    override fun initSpark() {
        spark.Spark.get("/info", this::customResponse)
        get("/toggle", this::toggleAll)
        spark.Spark.get("/state", this::state)
        spark.Spark.get("/users", this::users)
        spark.Spark.get("/quotes", this::quotes)
        spark.Spark.get("/lamps", this::lamps)
    }

    private fun customResponse(request: Request, response: Response): String {
        response.header("Content-Type", Transformer.contentType)
        val args: List<String> = request.queryParams("keys")?.split(delimiters = ",") ?: listOf()
        return if (args.isNotEmpty()) {
            ResponseCache.getKeys(args)
        } else {
            ResponseCache.json
        }
    }

    private fun toggleAll(request: Request, response: Response) {
        val user = SettingsManager.getUser(request)
        val enabled = ModuleManager.isEnabled(user)
        Bus.post(ToggleEvent(!enabled, user))
    }

    private fun state(req: Request, res: Response): String {
        val user = SettingsManager.getUser(req)
        val enabled = ModuleManager.isEnabled(user)
        return if (enabled) "1" else "0"
    }

    private fun users(req: Request, res: Response): String {
        res.header("Content-Type", Transformer.contentType)
        return SettingsManager.jsonUsers
    }

    private fun quotes(req: Request, res: Response): String {
        res.header("Content-Type", Transformer.contentType)
        return SettingsManager.jsonQuotes
    }

    private fun lamps(req: Request, res: Response): String {
        res.header("Content-Type", Transformer.contentType)
        return SettingsManager.jsonLamps
    }
}