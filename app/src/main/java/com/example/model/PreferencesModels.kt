package com.example.model

enum class AppThemeMode(val displayName: String) {
    BLUE("Royal Blue"),
    PURPLE("Vibrant Purple"),
    GREEN("Emerald Green"),
    ORANGE("Sunset Orange"),
    DARK_NAVY("Dark Navy"),
    SYSTEM_DARK("Dark Mode")
}

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    HINDI("hi", "हिन्दी (Hindi)")
}

data class NotificationPreferences(
    val upcomingClass: Boolean = true,
    val homeworkDeadline: Boolean = true,
    val examReminder: Boolean = true,
    val studyGoal: Boolean = true,
    val studySession: Boolean = true,
    val attendanceWarning: Boolean = true
)
