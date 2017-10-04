package nl.sjtek.control.core.media

import io.habets.artparser.ArtParser
import io.habets.artparser.models.Result
import nl.sjtek.control.core.net.HttpClient
import nl.sjtek.control.core.settings.SettingsManager

object ArtParserHolder {
    private val artParser = ArtParser(
            clientId = SettingsManager.settings.artParser.clientId,
            clientSecret = SettingsManager.settings.artParser.clientSecret,
            redirectUri = "",
            httpClient = HttpClient.client,
            path = SettingsManager.settings.artParser.path,
            baseUrl = SettingsManager.settings.artParser.baseUrl)

    @Synchronized
    fun get(uri: String): Result? {
        return artParser.get(uri)
    }
}