package com.eduplay.moblie.useCases

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateConverter {
    companion object {
        private val serverDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SSS")
        private val presentingFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm")

        fun convertFromServerFormat(string: String): LocalDateTime {
            return LocalDateTime.parse(string, serverDateFormatter)
        }

        fun convertToServerFormat(date: LocalDateTime): String {
            return date.format(serverDateFormatter)
        }

        fun convertForDisplay(date: LocalDateTime): String {
            return date.format(presentingFormatter)
        }

        fun convertForDisplay(string: String): String {
            return LocalDateTime.parse(string, serverDateFormatter).format(presentingFormatter)
        }
    }
}