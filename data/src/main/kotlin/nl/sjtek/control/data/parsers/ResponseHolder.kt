package nl.sjtek.control.data.parsers

import nl.sjtek.control.data.response.*

@Suppress("MemberVisibilityCanPrivate")
data class ResponseHolder(val map: Map<String, Response>) {
    val audio: Audio = map["audio"] as Audio
    val base: Base = map["base"] as Base
    val coffee: Coffee = map["coffee"] as Coffee
    val lights: Lights = map["lights"] as Lights
    val music: Music = map["music"] as Music
    val nightMode: NightMode = map["nightmode"] as NightMode
    val temperature: Temperature = map["temperature"] as Temperature
    val tv: TV = map["tv"] as TV

    @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
    internal fun test() {
        audio!!
        base!!
        coffee!!
        lights!!
        music!!
        nightMode!!
        temperature!!
        tv!!
    }
}