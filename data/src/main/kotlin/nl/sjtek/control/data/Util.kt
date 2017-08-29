package nl.sjtek.control.data

fun Int.toBoolean(): Boolean = this == 1
fun Boolean.toInt(): Int = if (this) 1 else 0