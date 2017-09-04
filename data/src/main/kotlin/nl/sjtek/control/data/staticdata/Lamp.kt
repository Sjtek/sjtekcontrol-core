package nl.sjtek.control.data.staticdata

import java.io.Serializable

data class Lamp(val name: String,
                val visibleName: String,
                val internalId: Int,
                val rgb: Boolean,
                val room: String,
                var state: Boolean = false,
                val sensorId: Int = -1,
                val owner: String? = null,
                val onlyOff: Boolean = false) : Serializable