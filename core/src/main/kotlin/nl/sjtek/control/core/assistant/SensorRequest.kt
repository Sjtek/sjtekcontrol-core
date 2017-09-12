package nl.sjtek.control.core.assistant

data class SensorRequest(val location: Location, val type: Type) : AssistantRequest() {
    enum class Location {
        INSIDE, OUTSIDE;

        override fun toString(): String {
            return super.toString().toLowerCase()
        }
    }

    enum class Type {
        TEMPERATURE, HUMIDITY;

        override fun toString(): String {
            return super.toString().toLowerCase()
        }
    }
}