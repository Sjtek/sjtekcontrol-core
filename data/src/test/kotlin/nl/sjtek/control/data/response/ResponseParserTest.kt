package nl.sjtek.control.data.response

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

internal class ResponseParserTest {
    @Test
    fun parse() {
        val map = ResponseParser.parse(example)
        assertNotNull(map)
    }

    companion object {
        const val example = """
            {
  "audio": {
    "type": "nl.sjtek.control.data.response.Audio",
    "key": "audio",
    "enabled": false,
    "modules": {
      "tv": false,
      "music": false
    }
  },
  "temperature": {
    "type": "nl.sjtek.control.data.response.Temperature",
    "key": "temperature",
    "insideTemperature": 26.75,
    "insideHumidity": 0.0,
    "outsideTemperature": 22.455555,
    "outsideHumidity": 0.65
  },
  "tv": {
    "type": "nl.sjtek.control.data.response.TV",
    "key": "tv",
    "enabled": false
  },
  "base": {
    "type": "nl.sjtek.control.data.response.Base",
    "key": "base"
  },
  "lights": {
    "type": "nl.sjtek.control.data.response.Lights",
    "key": "lights",
    "state": {
      "1": true,
      "2": true,
      "3": true,
      "4": false,
      "5": false,
      "6": false,
      "7": true,
      "8": true,
      "9": true
    }
  },
  "nightmode": {
    "type": "nl.sjtek.control.data.response.NightMode",
    "key": "nightmode",
    "enabled": false
  },
  "coffee": {
    "type": "nl.sjtek.control.data.response.Coffee",
    "key": "coffee",
    "enabled": false,
    "lastTimeEnabled": 0
  },
  "music": {
    "type": "nl.sjtek.control.data.response.Music",
    "key": "music",
    "connected": true,
    "state": "STOPPED",
    "name": "",
    "artist": "",
    "album": "",
    "uri": "",
    "albumArt": "",
    "artistArt": "",
    "volume": -1
  }
}
            """
    }
}