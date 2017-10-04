package nl.sjtek.control.core.modules

import com.google.gson.GsonBuilder
import nl.sjtek.control.core.media.ArtParserHolder
import nl.sjtek.control.data.response.Response
import nl.sjtek.control.data.staticdata.User
import spark.Spark.*
import nl.sjtek.control.data.response.Art as ArtResponse

class Art(key: String) : Module(key) {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    override val response: Response
        get() = ArtResponse(key, ArtParserHolder.artParser.tracksSize, ArtParserHolder.artParser.artistsSize, ArtParserHolder.artParser.albumsSize)

    override fun isEnabled(user: User?): Boolean = false

    override fun initSpark() {
        path("/art") {
            get("/get", this::get)
        }
    }

    private fun get(req: spark.Request, res: spark.Response): String {
        val uri: String = req.queryMap()["uri"].value() ?: throw halt(400)
        val result = ArtParserHolder.artParser.get(uri) ?: throw halt(404)
        res.header("Content-Type", "application/json")
        return gson.toJson(result)
    }
}