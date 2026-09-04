package com.example.vitallog.data

import java.time.LocalDate

object LoginData {

    val loginDates = mutableSetOf<LocalDate>(
        LocalDate.now().minusDays(1), 
        LocalDate.now().minusDays(2)  
    )

    fun recordLogin() {
        loginDates.add(LocalDate.now()) 
    }
}
