package com.example.vitallog.data

import androidx.compose.runtime.mutableStateSetOf
import java.time.LocalDate

object LoginData {

    val loginDates = mutableStateSetOf(
        LocalDate.now().minusDays(1), // Yesterday (e.g., Sep 3)
        LocalDate.now().minusDays(2)  // Day before yesterday (e.g., Sep 2)
    )

    fun recordLogin() {
        loginDates.add(LocalDate.now()) // Today (e.g., Sep 4)
    }
}
