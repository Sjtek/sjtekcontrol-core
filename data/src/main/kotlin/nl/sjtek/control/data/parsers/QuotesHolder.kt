package nl.sjtek.control.data.parsers

import java.io.Serializable

data class QuotesHolder(val quotes: List<String> = listOf(), @Transient val exception: Exception? = null) : Serializable