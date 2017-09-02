package nl.sjtek.control.data.parsers

data class QuotesHolder(val quotes: List<String> = listOf(), @Transient val exception: Exception? = null)