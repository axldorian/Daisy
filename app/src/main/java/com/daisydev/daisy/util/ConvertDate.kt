package com.daisydev.daisy.util

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date

/**
 * Convierte una fecha en formato ISO 8601 a un objeto Date
 * @param dateString Fecha en formato ISO 8601
 * @return Objeto Date
 */
fun convertStringToDate(dateString: String): Date {
    val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    val offsetDateTime = OffsetDateTime.parse(dateString, formatter)
    val utcDateTime = offsetDateTime.withOffsetSameInstant(ZoneOffset.UTC)

    val cmdxZoneId = ZoneId.of("America/Mexico_City")
    val cdmxDateTime = LocalDateTime.ofInstant(utcDateTime.toInstant(), cmdxZoneId)
    val cdmxOffsetDateTime =
        OffsetDateTime.of(cdmxDateTime, cmdxZoneId.rules.getOffset(cdmxDateTime))

    return Date.from(cdmxOffsetDateTime.toInstant())
}