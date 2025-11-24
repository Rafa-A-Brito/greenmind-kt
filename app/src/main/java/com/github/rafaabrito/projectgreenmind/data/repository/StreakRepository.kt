package com.github.rafaabrito.projectgreenmind.data.repository

import com.github.rafaabrito.projectgreenmind.domain.dao.StreakDao
import com.github.rafaabrito.projectgreenmind.domain.entities.StreakEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class StreakRepository @Inject constructor(
    private val streakDao: StreakDao
) {
    fun getStreakByUserId(userId: Int): Flow<StreakEntity?> {
        return streakDao.getStreakByUserId(userId)
    }

    suspend fun checkInToday(userId: Int): StreakCheckInResult {
        val today = getTodayDate()
        val streak = streakDao.getStreakByUserId(userId).first()

        return if (streak == null) {
            // Primeiro check-in
            val newStreak = StreakEntity(
                userId = userId,
                currentStreak = 1,
                longestStreak = 1,
                lastCheckInDate = today,
                totalCheckIns = 1
            )
            streakDao.insertOrUpdateStreak(newStreak)
            StreakCheckInResult.NewStreak(1)
        } else {
            when {
                streak.lastCheckInDate == today -> {
                    // Já fez check-in hoje
                    StreakCheckInResult.AlreadyCheckedIn(streak.currentStreak)
                }
                isYesterday(streak.lastCheckInDate) -> {
                    // Check-in consecutivo
                    val newCurrentStreak = streak.currentStreak + 1
                    val newLongestStreak = maxOf(newCurrentStreak, streak.longestStreak)

                    val updatedStreak = streak.copy(
                        currentStreak = newCurrentStreak,
                        longestStreak = newLongestStreak,
                        lastCheckInDate = today,
                        totalCheckIns = streak.totalCheckIns + 1
                    )
                    streakDao.insertOrUpdateStreak(updatedStreak)
                    StreakCheckInResult.StreakContinued(newCurrentStreak, newCurrentStreak == newLongestStreak)
                }
                else -> {
                    // Streak quebrada
                    val updatedStreak = streak.copy(
                        currentStreak = 1,
                        lastCheckInDate = today,
                        totalCheckIns = streak.totalCheckIns + 1
                    )
                    streakDao.insertOrUpdateStreak(updatedStreak)
                    StreakCheckInResult.StreakBroken(streak.currentStreak)
                }
            }
        }
    }

    private fun getTodayDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun isYesterday(dateString: String): Boolean {
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = dateFormat.parse(dateString) ?: return false
            val yesterday = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -1)
            }.time

            val cal1 = Calendar.getInstance().apply { time = date }
            val cal2 = Calendar.getInstance().apply { time = yesterday }

            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
        } catch (e: Exception) {
            return false
        }
    }
}

sealed class StreakCheckInResult {
    data class NewStreak(val days: Int) : StreakCheckInResult()
    data class StreakContinued(val currentStreak: Int, val isNewRecord: Boolean) : StreakCheckInResult()
    data class StreakBroken(val previousStreak: Int) : StreakCheckInResult()
    data class AlreadyCheckedIn(val currentStreak: Int) : StreakCheckInResult()
}