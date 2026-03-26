package com.example.picturepuzzle.utils

import android.content.Context
import java.time.LocalDate

class DailyCheckInManager(context: Context) {

    private val prefs = context.getSharedPreferences("daily_checkin", Context.MODE_PRIVATE)

    fun checkInIfNeeded(): CheckInResult {
        val today = LocalDate.now().toString()
        val lastDate = prefs.getString("last_date", null)
        var streak = prefs.getInt("streak", 0)
        val best = prefs.getInt("best_streak", 0)

        if (today == lastDate) {
            return CheckInResult(streak, best, false)
        }

        if (lastDate != null) {
            val last = LocalDate.parse(lastDate)
            val diff = last.plusDays(1).toString() == today
            streak = if (diff) streak + 1 else 1
        } else {
            streak = 1
        }

        val newBest = maxOf(best, streak)

        prefs.edit()
            .putString("last_date", today)
            .putInt("streak", streak)
            .putInt("best_streak", newBest)
            .apply()

        return CheckInResult(streak, newBest, true)
    }
}

data class CheckInResult(
    val streak: Int,
    val bestStreak: Int,
    val isNewCheckIn: Boolean
)