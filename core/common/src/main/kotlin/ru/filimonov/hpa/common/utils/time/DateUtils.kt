package ru.filimonov.hpa.common.utils.time

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

object DateUtils {
    const val DMY_DATE = "dd.MM.yyyy"
    const val DMY_DATETIME = "dd.MM.yyyy HH:mm"
    const val HM_TIME = "HH:mm"

    @JvmStatic
    fun Long.toCalendar(): Calendar =
        Calendar.getInstance().apply { timeInMillis = this@toCalendar }

    @JvmStatic
    fun ZonedDateTime.isNotPast(): Boolean = this.isAfter(ZonedDateTime.now(this.zone))

    @JvmStatic
    fun ZonedDateTime.isPast(): Boolean = !isNotPast()

    @JvmStatic
    fun ZonedDateTime?.toString(pattern: String): String {
        return if (this == null)
            ""
        else
            DateTimeFormatter.ofPattern(pattern).withLocale(Locale.getDefault())
                .format(this)
    }

    @JvmStatic
    fun LocalDate?.toString(pattern: String): String {
        return if (this == null)
            ""
        else
            DateTimeFormatter.ofPattern(pattern).withLocale(Locale.getDefault())
                .format(this)
    }

    @JvmStatic
    fun LocalTime?.toString(pattern: String): String {
        return if (this == null)
            ""
        else
            DateTimeFormatter.ofPattern(pattern).withLocale(Locale.getDefault())
                .format(this)
    }

    @JvmStatic
    fun Long.toZonedDateTime(zoneId: ZoneId = ZoneId.systemDefault()) =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(this), zoneId)

    @JvmStatic
    fun Long.toZonedDateTime(otherDate: ZonedDateTime) =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(this), otherDate.zone)

    @JvmStatic
    fun ZonedDateTime.toEpochMillis() = this.toInstant().toEpochMilli()

    @JvmStatic
    fun nowZonedDateTime(otherDate: ZonedDateTime) = ZonedDateTime.now(otherDate.zone)

    @JvmStatic
    fun Pair<Long, ZoneId>.toZonedDateTime() =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(first), second)

    @JvmStatic
    fun maxZonedDateTime(): ZonedDateTime {
        return ZonedDateTime.of(999999999, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault())
    }

    @JvmStatic
    fun minZonedDateTime(): ZonedDateTime {
        return ZonedDateTime.ofInstant(Instant.MIN, ZoneId.systemDefault())
    }

    @JvmStatic
    fun todayLastMoment(zone: ZoneId = ZoneId.systemDefault()): ZonedDateTime =
        ZonedDateTime.of(
            LocalDate.now(),
            LocalTime.MAX,
            zone
        )

    @JvmStatic
    fun today(zone: ZoneId = ZoneId.systemDefault()): ZonedDateTime =
        ZonedDateTime.of(
            LocalDate.now(),
            LocalTime.MIN,
            zone
        )

    fun LocalDate.getDayLastMoment(
        zone: ZoneId = ZoneId.systemDefault()
    ): ZonedDateTime =
        ZonedDateTime.of(
            this,
            LocalTime.MAX,
            zone
        )

    fun LocalDate.toEpochMillis(
        zone: ZoneId = ZoneId.systemDefault(),
        localTime: LocalTime = LocalTime.MIN
    ): Long = ZonedDateTime.of(this, localTime, zone).toEpochMillis()

    fun Long.toLocalDate(zone: ZoneId = ZoneId.systemDefault()): LocalDate {
        return Instant.ofEpochMilli(this).atZone(zone).toLocalDate()
    }

    fun Long.toLocalTime(zone: ZoneId = ZoneId.systemDefault()): LocalTime {
        return Instant.ofEpochMilli(this).atZone(zone).toLocalTime()
    }

    fun LocalTime.toEpochMillis(): Long {
        return this.toNanoOfDay() / 1000
    }

    fun ZonedDateTime?.updateDate(newDate: LocalDate): ZonedDateTime {
        return ZonedDateTime.of(
            newDate,
            this?.toLocalTime() ?: LocalTime.MIN,
            this?.zone ?: ZoneId.systemDefault()
        )
    }

    fun ZonedDateTime?.updateTime(newTime: LocalTime): ZonedDateTime {
        return ZonedDateTime.of(
            this?.toLocalDate() ?: LocalDate.now(),
            newTime,
            this?.zone ?: ZoneId.systemDefault()
        )
    }
}
