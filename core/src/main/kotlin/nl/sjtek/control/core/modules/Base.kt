package nl.sjtek.control.core.modules

import nl.sjtek.control.core.ModuleManager
import nl.sjtek.control.core.events.Bus
import nl.sjtek.control.core.events.ToggleEvent
import nl.sjtek.control.core.settings.Settings
import nl.sjtek.control.core.settings.UserManager
import spark.Request
import spark.Response
import spark.Spark.get
import spark.kotlin.get
import nl.sjtek.control.data.response.Base as BaseResponse

class Base(key: String, settings: Settings) : Module(key, settings) {

    override val response: BaseResponse
        get() = BaseResponse(true)

    override fun initSpark() {
        get("info") {}
        get("toggle", this::toggleAll)
    }

    private fun toggleAll(request: Request, response: Response) {
        val user = UserManager.get(request)
        val enabled = ModuleManager.isEnabled(user)
        Bus.post(ToggleEvent(!enabled))
    }
}