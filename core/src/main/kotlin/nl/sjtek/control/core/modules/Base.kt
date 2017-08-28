package nl.sjtek.control.core.modules

import nl.sjtek.control.core.ModuleManager
import nl.sjtek.control.core.events.Bus
import nl.sjtek.control.core.events.ToggleEvent
import nl.sjtek.control.core.get
import nl.sjtek.control.core.response.ResponseCache
import nl.sjtek.control.core.response.Transformer
import nl.sjtek.control.core.settings.UserManager
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
        val user = UserManager.get(request)
        val enabled = ModuleManager.isEnabled(user)
        Bus.post(ToggleEvent(!enabled))
    }

    private fun state(req: Request, res: Response): String {
        val user = UserManager.get(req)
        val enabled = ModuleManager.isEnabled(user)
        res.header("Content-Type", Transformer.contentType)
        return "{\"state\": $enabled}"
    }
}