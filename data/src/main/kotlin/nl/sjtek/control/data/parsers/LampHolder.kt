package nl.sjtek.control.data.parsers

import nl.sjtek.control.data.staticdata.Lamp

data class LampHolder(val lamps: Map<Int, Lamp> = mapOf(), @Transient val exception: Exception? = null)