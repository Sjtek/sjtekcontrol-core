package nl.sjtek.control.core

import nl.sjtek.control.core.settings.SettingsManager
import org.slf4j.LoggerFactory
import kotlin.system.measureTimeMillis


fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("SjtekControl Core")
    logger.info("Initialising...")
    val initTime = measureTimeMillis {
        SettingsManager.settings
        ModuleManager.init()
    }
    logger.info("Sjtek control core started in ${initTime}ms")
}