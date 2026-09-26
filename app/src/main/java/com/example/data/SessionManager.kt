package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AppLanguage
import com.example.model.AppThemeMode
import com.example.model.NotificationPreferences
import com.example.model.User
import com.example.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("studymate_prefs", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<User?>(loadUser())
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _themeMode = MutableStateFlow(loadThemeMode())
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _language = MutableStateFlow(loadLanguage())
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _notificationPrefs = MutableStateFlow(loadNotificationPreferences())
    val notificationPrefs: StateFlow<NotificationPreferences> = _notificationPrefs.asStateFlow()

    private fun loadUser(): User? {
        val userJson = prefs.getString("user_json", null) ?: return null
        return try {
            val json = JSONObject(userJson)
            User(
                id = json.optString("id"),
                name = json.optString("name"),
                email = json.optString("email"),
                phone = json.optString("phone"),
                role = if (json.optString("role") == "ADMIN") UserRole.ADMIN else UserRole.STUDENT,
                className = json.optString("className"),
                schoolName = json.optString("schoolName"),
                profileImage = json.optString("profileImage"),
                bio = json.optString("bio"),
                createdAt = json.optLong("createdAt", System.currentTimeMillis())
            )
        } catch (e: Exception) {
            null
        }
    }

    fun saveUser(user: User, rememberMe: Boolean = true) {
        val json = JSONObject().apply {
            put("id", user.id)
            put("name", user.name)
            put("email", user.email)
            put("phone", user.phone)
            put("role", user.role.name)
            put("className", user.className)
            put("schoolName", user.schoolName)
            put("profileImage", user.profileImage)
            put("bio", user.bio)
            put("createdAt", user.createdAt)
        }
        prefs.edit().apply {
            if (rememberMe) {
                putString("user_json", json.toString())
            } else {
                remove("user_json")
            }
            apply()
        }
        _currentUser.value = user
    }

    fun logout() {
        prefs.edit().remove("user_json").apply()
        _currentUser.value = null
    }

    private fun loadThemeMode(): AppThemeMode {
        val name = prefs.getString("theme_mode", AppThemeMode.BLUE.name)
        return try {
            AppThemeMode.valueOf(name ?: AppThemeMode.BLUE.name)
        } catch (e: Exception) {
            AppThemeMode.BLUE
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _themeMode.value = mode
    }

    private fun loadLanguage(): AppLanguage {
        val code = prefs.getString("lang_code", AppLanguage.ENGLISH.code)
        return if (code == "hi") AppLanguage.HINDI else AppLanguage.ENGLISH
    }

    fun setLanguage(lang: AppLanguage) {
        prefs.edit().putString("lang_code", lang.code).apply()
        _language.value = lang
    }

    private fun loadNotificationPreferences(): NotificationPreferences {
        return NotificationPreferences(
            upcomingClass = prefs.getBoolean("notif_class", true),
            homeworkDeadline = prefs.getBoolean("notif_hw", true),
            examReminder = prefs.getBoolean("notif_exam", true),
            studyGoal = prefs.getBoolean("notif_goal", true),
            studySession = prefs.getBoolean("notif_session", true),
            attendanceWarning = prefs.getBoolean("notif_attendance", true)
        )
    }

    fun updateNotificationPreferences(notifs: NotificationPreferences) {
        prefs.edit()
            .putBoolean("notif_class", notifs.upcomingClass)
            .putBoolean("notif_hw", notifs.homeworkDeadline)
            .putBoolean("notif_exam", notifs.examReminder)
            .putBoolean("notif_goal", notifs.studyGoal)
            .putBoolean("notif_session", notifs.studySession)
            .putBoolean("notif_attendance", notifs.attendanceWarning)
            .apply()
        _notificationPrefs.value = notifs
    }
}
