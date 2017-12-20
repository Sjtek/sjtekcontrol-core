package nl.sjtek.control.data.response

import nl.sjtek.control.data.parsers.ResponseParser
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
  "temperature": {
    "type": "nl.sjtek.control.data.response.Temperature",
    "key": "temperature",
    "insideTemperature": 20.5,
    "insideHumidity": 0.0,
    "outsideTemperature": 7.288888,
    "outsideHumidity": 0.99
  },
  "music": {
    "type": "nl.sjtek.control.data.response.Music",
    "key": "music",
    "connected": true,
    "state": "PAUSED",
    "name": "Life In The Fast Lane - Remastered",
    "artist": "Eagles",
    "album": "Hotel California (Remastered)",
    "uri": "spotify:track:6gXrEUzibufX9xYPk3HD5p",
    "albumArt": "https://sjtek.nl/cache/albums/2widuo17g5CEC66IbzveRu.jpg",
    "artistArt": "https://sjtek.nl/cache/artists/0ECwFtbIWEVNwjlrfc6xoL.jpg",
    "volume": 61,
    "red": 33,
    "green": 24,
    "blue": 29
  },
  "audio": {
    "type": "nl.sjtek.control.data.response.Audio",
    "key": "audio",
    "enabled": true,
    "modules": {
      "music": false,
      "tv": true
    }
  },
  "base": {
    "type": "nl.sjtek.control.data.response.Base",
    "key": "base"
  },
  "lights": {
    "type": "nl.sjtek.control.data.response.Lights",
    "key": "lights",
    "state": {
      "1": false,
      "2": false,
      "3": false,
      "4": false,
      "5": false,
      "6": false,
      "7": false,
      "8": false,
      "9": false
    }
  },
  "nightmode": {
    "type": "nl.sjtek.control.data.response.NightMode",
    "key": "nightmode",
    "enabled": false
  },
  "tv": {
    "type": "nl.sjtek.control.data.response.TV",
    "key": "tv",
    "enabled": true
  },
  "coffee": {
    "type": "nl.sjtek.control.data.response.Coffee",
    "key": "coffee",
    "enabled": false,
    "lastTimeEnabled": 0
  },
  "assistant": {
    "type": "nl.sjtek.control.data.response.Assistant",
    "key": "assistant"
  },
  "color": {
    "type": "nl.sjtek.control.data.response.Color",
    "key": "color",
    "lamps": []
  },
  "art": {
    "type": "nl.sjtek.control.data.response.Art",
    "key": "art",
    "tracks": 143,
    "artists": 97,
    "albums": 113
  },
  "screen": {
    "type": "nl.sjtek.control.data.response.Screen",
    "key": "screen",
    "video": false
  }
}
            """
    }
}
