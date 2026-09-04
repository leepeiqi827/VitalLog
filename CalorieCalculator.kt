package com.example.vitallog.util

/**
 * Estimates calories burned for a logged workout using standard MET
 * (Metabolic Equivalent of Task) values, adjusted for the user's body
 * weight, workout duration, and self-reported intensity.
 *
 * Formula: calories = MET x weight(kg) x duration(hours) x intensityFactor
 */
object CalorieCalculator {

    private val metByWorkoutType = mapOf(
        "Cycling" to 7.5,
        "Swim" to 6.0,
        "Yoga" to 3.0,
        "Weighing" to 3.5, // strength/weight training
        "Jogging" to 7.0,
        "Skipping" to 10.0
    )

    private val intensityFactor = mapOf(
        "Low" to 0.8,
        "Medium" to 1.0,
        "High" to 1.2
    )

    private const val DEFAULT_MET = 5.0

    fun estimateCalories(
        workoutType: String,
        durationMinutes: Int,
        intensity: String,
        weightKg: Double
    ): Int {
        val met = metByWorkoutType[workoutType] ?: DEFAULT_MET
        val factor = intensityFactor[intensity] ?: 1.0
        val hours = durationMinutes / 60.0
        val calories = met * factor * weightKg * hours
        return calories.toInt().coerceAtLeast(0)
    }
}
