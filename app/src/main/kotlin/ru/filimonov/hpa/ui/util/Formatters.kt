package ru.filimonov.hpa.ui.util

import kotlin.math.roundToInt

fun Double.formatAsTemperature(): String {
    return "+%d%s".format(this.roundToInt(), "°C")
}

fun Float.formatAsTemperature(): String {
    return this.toDouble().formatAsTemperature()
}

fun Int.formatAsPercents(): String {
    return "$this%"
}

fun Double.toString(afterComma: Int): String {
    return "%0${afterComma}.${afterComma}f".format(this)
}

fun Double.formatAsPercents(afterComma: Int): String {
    return if (afterComma == 0)
        "${this.roundToInt()}%"
    else
        "%0${afterComma}.${afterComma}f%%".format(this)
}

fun Float.formatAsPercents(afterComma: Int): String {
    return this.toDouble().formatAsPercents(afterComma)
}
