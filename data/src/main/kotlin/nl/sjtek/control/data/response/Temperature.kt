package nl.sjtek.control.data.response

data class Temperature(
        override val key: String,
        val insideTemperature: Float,
        val insideHumidity: Float,
        val outsideTemperature: Float,
        val outsideHumidity: Float) : Response() {
    override val type: String = javaClass.canonicalName
}