package nl.sjtek.control.core

import nl.sjtek.control.core.settings.SettingsManager


fun main(args: Array<String>) {
    println("Sjtek control core")
    SettingsManager.settings
    ModuleManager.init()
}