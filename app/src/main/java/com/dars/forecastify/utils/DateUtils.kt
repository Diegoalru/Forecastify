package com.dars.forecastify.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DateUtils {
    /**
     * Convierte una fecha en formato Unix a un formato legible.
     * @param unix Fecha en formato Unix.
     * @param fullDate Si se desea obtener la fecha completa o solo la hora.
     * @return Fecha en formato legible.
     */
    fun convertUnixDate(unix: Int, fullDate: Boolean): String {
        val date = Date(unix.toLong() * 1000L)
        val format = if (fullDate) "dd/MM/yyyy hh:mm a" else "hh:mm a"
        val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
        simpleDateFormat.timeZone = TimeZone.getDefault()
        return simpleDateFormat.format(date)
    }
}