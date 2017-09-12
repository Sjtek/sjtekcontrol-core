package nl.sjtek.control.core

import org.slf4j.LoggerFactory

object Executor {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(command: Array<String>): Int {
        try {
            val process = Runtime.getRuntime().exec(command)
            process.waitFor()
            return process.exitValue()
        } catch (e: Exception) {
            logger.error("Execution error", e)
            return 1
        }
    }
}