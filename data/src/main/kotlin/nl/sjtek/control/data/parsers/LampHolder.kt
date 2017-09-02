package nl.sjtek.control.data.parsers

import nl.sjtek.control.data.static.Lamp

data class LampHolder(val lamps: Map<Int, Lamp> = mapOf(), @Transient val exception: Exception? = null)