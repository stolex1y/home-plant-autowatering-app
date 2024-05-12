package ru.filimonov.hpa.ui.device.details.model

import androidx.compose.ui.text.intl.Locale
import ru.filimonov.hpa.common.utils.time.DateUtils.toEpochMillis
import ru.filimonov.hpa.common.utils.time.DateUtils.toMediumFormatString
import ru.filimonov.hpa.domain.model.readings.DomainSensorReadings
import ru.filimonov.hpa.ui.util.formatAsPercents
import ru.filimonov.hpa.ui.util.formatAsTemperature
import java.time.ZonedDateTime

data class SensorReadings(
    val timestamp: String,
    val soilMoisture: String,
    val airHumidity: String,
    val airTemp: String,
    val lightLevel: String,
    val batteryCharge: String,
) {
    companion object {
        fun DomainSensorReadings.toSensorReadings(): SensorReadings {
            val minDate = arrayOf(soilMoisture?.timestamp, ZonedDateTime.now()).filterNotNull()
                .minByOrNull { it.toEpochMillis() }!!
            return SensorReadings(
                timestamp = minDate.toMediumFormatString(locale = Locale.current.toLanguageTag()),
                soilMoisture = soilMoisture?.reading?.formatAsPercents(0) ?: "—",
                airHumidity = airHumidity?.reading?.formatAsPercents(0) ?: "—",
                airTemp = airTemp?.reading?.formatAsTemperature() ?: "—",
                lightLevel = lightLevel?.reading?.toString() ?: "—",
                batteryCharge = batteryCharge?.reading?.formatAsPercents(0) ?: "—",
            )
        }
    }
}
