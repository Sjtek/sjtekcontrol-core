package nl.sjtek.control.core

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator
import com.luckycatlabs.sunrisesunset.dto.Location
import nl.sjtek.control.core.settings.SettingsManager
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

object SunsetCalculator {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val calculator: SunriseSunsetCalculator
    private val timeZoneString: String
    private val timeZone: TimeZone

    val now: Calendar
        get() {
            return Calendar.getInstance(timeZone)
        }
    val nowHour: Int
        get() {
            val instant = Instant.now()
            val zoneId = ZoneId.of(timeZoneString)
            val zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId)
            return zonedDateTime.hour
        }
    val sunrise: Int
        get() = calculator.getCivilSunriseCalendarForDate(now).get(Calendar.HOUR_OF_DAY)
    val sunset: Int
        get() = calculator.getCivilSunsetCalendarForDate(now).get(Calendar.HOUR_OF_DAY)
    val night: Boolean
        get() {
            logger.info("now: $nowHour sunrise: $sunrise sunset: $sunset")
            return nowHour >= sunset || nowHour <= sunrise
        }

    init {
        val settings = SettingsManager.settings.sunset
        val location = Location(settings.latitude, settings.longitude)
        timeZoneString = settings.timeZone
        timeZone = TimeZone.getTimeZone(timeZoneString)
        calculator = SunriseSunsetCalculator(location, timeZone)
    }
}
