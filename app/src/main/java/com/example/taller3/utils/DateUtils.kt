package com.example.taller3.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DateUtils {
    companion object {
        private const val DEFAULT_PATTERN = "yyyy-MM-dd HH:mm"

        private const val TIME_PATTERN = "hh:mm a"

        // Time only when the date is today, full date otherwise.
        fun formatRecent(millis: Long, now: Long = System.currentTimeMillis()): String =
            if (isSameDay(millis, now)) format(millis, TIME_PATTERN) else format(millis)

        private fun isSameDay(a: Long, b: Long): Boolean {
            val first = Calendar.getInstance().apply { timeInMillis = a }
            val second = Calendar.getInstance().apply { timeInMillis = b }
            return first.get(Calendar.YEAR) == second.get(Calendar.YEAR) &&
                first.get(Calendar.DAY_OF_YEAR) == second.get(Calendar.DAY_OF_YEAR)
        }

        fun format(millis: Long, pattern: String = DEFAULT_PATTERN): String =
            SimpleDateFormat(pattern, Locale.getDefault()).format(Date(millis))
    }
}
