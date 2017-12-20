package nl.sjtek.control.core.modules

import nl.sjtek.control.core.get
import nl.sjtek.control.core.response.ResponseCache
import nl.sjtek.control.data.response.Response
import nl.sjtek.control.data.response.Screen
import nl.sjtek.control.data.staticdata.User
import spark.Spark.path

class Screen(key: String) : Module(key) {
    override val response: Response
        get() = Screen(key, videoEnabled)

    override fun isEnabled(user: User?): Boolean {
        return false
    }

    override fun initSpark() {
        path("/screen") {
            get("/enable", this::videoEnable)
            get("/disable", this::videoDisable)
        }
    }

    private var videoEnabled = false

    private fun videoEnable(req: spark.Request, res: spark.Response) {
        videoEnabled = true
        ResponseCache.post(this, true)
    }

    private fun videoDisable(req: spark.Request, res: spark.Response) {
        videoEnabled = false
        ResponseCache.post(this, true)
    }
}