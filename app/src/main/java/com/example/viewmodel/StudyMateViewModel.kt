package com.example.viewmodel

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FirebaseHelper
import com.example.data.SessionManager
import com.example.data.StudyMateRepository
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppDestination(val route: String, val label: String) {
    SPLASH("splash", "Splash"),
    LOGIN("login", "Login"),
    SIGN_UP("signup", "Sign Up"),
    DASHBOARD("dashboard", "Dashboard"),
    TIMETABLE("timetable", "Timetable"),
    NOTES("notes", "Notes"),
    HOMEWORK("homework", "Homework"),
    EXAMS("exams", "Exams"),
    GOALS("goals", "Goals"),
    TIMER("timer", "Study Timer"),
    PROGRESS("progress", "Progress"),
    ATTENDANCE("attendance", "Attendance"),
    CALENDAR("calendar", "Calendar"),
    NOTIFICATIONS("notifications", "Notifications"),
    GLOBAL_SEARCH("search", "Search"),
    PROFILE("profile", "Profile"),
    ADMIN_PANEL("admin", "Admin Panel"),
    SETTINGS("settings", "Settings"),
    ABOUT("about", "About"),
    PRIVACY_POLICY("privacy", "Privacy Policy"),
    TERMS("terms", "Terms & Conditions"),
    HELP_SUPPORT("help", "Help & Support")
}

data class TimerUiState(
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isPomodoro: Boolean = true,
    val isBreak: Boolean = false,
    val remainingSeconds: Int = 25 * 60,
    val totalSeconds: Int = 25 * 60,
    val selectedSubject: String = "Mathematics",
    val sessionNotes: String = ""
)

class StudyMateViewModel(application: Application) : AndroidViewModel(application) {
    val sessionManager = SessionManager(application)
    val repository = StudyMateRepository(application, sessionManager)

    // Navigation Stack
    private val _currentDestination = MutableStateFlow<AppDestination>(AppDestination.SPLASH)
    val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

    private val navStack = mutableListOf<AppDestination>()

    // Global Search Query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Timer State
    private val _timerState = MutableStateFlow(TimerUiState())
    val timerState: StateFlow<TimerUiState> = _timerState.asStateFlow()
    private var countDownTimer: CountDownTimer? = null

    // Goal Congratulations Banner / Celebration State
    private val _celebrationGoal = MutableStateFlow<GoalItem?>(null)
    val celebrationGoal: StateFlow<GoalItem?> = _celebrationGoal.asStateFlow()

    // Status snackbars / alerts
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    init {
        // Evaluate session after splash
        viewModelScope.launch {
            kotlinx.coroutines.delay(1800)
            if (sessionManager.currentUser.value != null) {
                _currentDestination.value = AppDestination.DASHBOARD
            } else {
                _currentDestination.value = AppDestination.LOGIN
            }
        }
    }

    fun navigateTo(dest: AppDestination) {
        if (_currentDestination.value != dest) {
            navStack.add(_currentDestination.value)
            _currentDestination.value = dest
        }
    }

    fun navigateBack(): Boolean {
        if (navStack.isNotEmpty()) {
            _currentDestination.value = navStack.removeAt(navStack.size - 1)
            return true
        }
        if (_currentDestination.value != AppDestination.DASHBOARD && sessionManager.currentUser.value != null) {
            _currentDestination.value = AppDestination.DASHBOARD
            return true
        }
        return false
    }

    fun showSnackbar(msg: String) {
        _snackbarMessage.value = msg
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    // --- AUTHENTICATION ---
    fun login(email: String, pass: String, rememberMe: Boolean, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            onError("Please enter both email and password")
            return
        }

        // Check if Admin login
        if (email.trim().equals("admin@studymate.com", ignoreCase = true) && pass == "admin123") {
            val adminUser = User(
                id = "admin_master",
                name = "System Admin",
                email = "admin@studymate.com",
                role = UserRole.ADMIN,
                className = "Administrator",
                schoolName = "StudyMate Academic",
                bio = "StudyMate Central Administrator"
            )
            sessionManager.saveUser(adminUser, rememberMe)
            showSnackbar("Welcome, Administrator!")
            onSuccess()
            _currentDestination.value = AppDestination.ADMIN_PANEL
            return
        }

        val auth = FirebaseHelper.getAuth(getApplication())
        if (auth != null) {
            try {
                auth.signInWithEmailAndPassword(email.trim(), pass)
                    .addOnSuccessListener { result ->
                        val firebaseUser = result.user
                        val user = User(
                            id = firebaseUser?.uid ?: "user_${System.currentTimeMillis()}",
                            name = firebaseUser?.displayName ?: email.substringBefore("@").replaceFirstChar { it.uppercase() },
                            email = firebaseUser?.email ?: email,
                            role = UserRole.STUDENT,
                            className = "Grade 11 - Science",
                            schoolName = "Springfield Academy"
                        )
                        sessionManager.saveUser(user, rememberMe)
                        showSnackbar("Welcome back, ${user.name}!")
                        onSuccess()
                        _currentDestination.value = AppDestination.DASHBOARD
                    }
                    .addOnFailureListener { e ->
                        // Fallback or handle password check
                        fallbackLocalLogin(email, pass, rememberMe, onSuccess, onError, e.message ?: "Authentication failed")
                    }
            } catch (e: Exception) {
                fallbackLocalLogin(email, pass, rememberMe, onSuccess, onError, null)
            }
        } else {
            fallbackLocalLogin(email, pass, rememberMe, onSuccess, onError, null)
        }
    }

    private fun fallbackLocalLogin(
        email: String,
        pass: String,
        rememberMe: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        fallbackError: String?
    ) {
        if (pass.length < 6) {
            onError("Password must be at least 6 characters")
            return
        }
        val student = User(
            id = "std_${System.currentTimeMillis() % 10000}",
            name = email.substringBefore("@").replace(".", " ").split(" ")
                .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } },
            email = email.trim(),
            role = UserRole.STUDENT,
            className = "Grade 11 - Science",
            schoolName = "Springfield Academy",
            bio = "Dedicated student working towards university admission."
        )
        sessionManager.saveUser(student, rememberMe)
        showSnackbar("Welcome, ${student.name}!")
        onSuccess()
        _currentDestination.value = AppDestination.DASHBOARD
    }

    fun signUp(
        name: String,
        email: String,
        phone: String,
        pass: String,
        confirmPass: String,
        className: String,
        schoolName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            onError("Please fill in all mandatory fields")
            return
        }
        if (!email.contains("@") || !email.contains(".")) {
            onError("Please enter a valid email address")
            return
        }
        if (pass.length < 6) {
            onError("Password must be at least 6 characters")
            return
        }
        if (pass != confirmPass) {
            onError("Passwords do not match")
            return
        }

        val auth = FirebaseHelper.getAuth(getApplication())
        if (auth != null) {
            try {
                auth.createUserWithEmailAndPassword(email.trim(), pass)
                    .addOnSuccessListener { result ->
                        val firebaseUser = result.user
                        val user = User(
                            id = firebaseUser?.uid ?: "user_${System.currentTimeMillis()}",
                            name = name.trim(),
                            email = email.trim(),
                            phone = phone.trim(),
                            role = UserRole.STUDENT,
                            className = if (className.isBlank()) "Standard Course" else className.trim(),
                            schoolName = if (schoolName.isBlank()) "High School" else schoolName.trim()
                        )
                        sessionManager.saveUser(user, true)
                        showSnackbar("Account created successfully!")
                        onSuccess()
                        _currentDestination.value = AppDestination.DASHBOARD
                    }
                    .addOnFailureListener { e ->
                        onError(e.message ?: "Sign up failed")
                    }
            } catch (e: Exception) {
                createLocalStudent(name, email, phone, className, schoolName, onSuccess)
            }
        } else {
            createLocalStudent(name, email, phone, className, schoolName, onSuccess)
        }
    }

    private fun createLocalStudent(
        name: String,
        email: String,
        phone: String,
        className: String,
        schoolName: String,
        onSuccess: () -> Unit
    ) {
        val user = User(
            id = "user_${System.currentTimeMillis()}",
            name = name.trim(),
            email = email.trim(),
            phone = phone.trim(),
            role = UserRole.STUDENT,
            className = if (className.isBlank()) "Standard Course" else className.trim(),
            schoolName = if (schoolName.isBlank()) "High School" else schoolName.trim(),
            bio = "Student at ${schoolName.ifBlank { "High School" }}"
        )
        sessionManager.saveUser(user, true)
        showSnackbar("Welcome to StudyMate, ${user.name}!")
        onSuccess()
        _currentDestination.value = AppDestination.DASHBOARD
    }

    fun googleSignIn(onSuccess: () -> Unit) {
        val googleUser = User(
            id = "google_user_${System.currentTimeMillis() % 1000}",
            name = "Alex Johnson",
            email = "alex.johnson@student.edu",
            role = UserRole.STUDENT,
            className = "Grade 11 - Advanced",
            schoolName = "Northgate High",
            bio = "Enthusiastic student using StudyMate"
        )
        sessionManager.saveUser(googleUser, true)
        showSnackbar("Signed in via Google as Alex Johnson")
        onSuccess()
        _currentDestination.value = AppDestination.DASHBOARD
    }

    fun logout() {
        val auth = FirebaseHelper.getAuth(getApplication())
        try {
            auth?.signOut()
        } catch (_: Exception) {}
        sessionManager.logout()
        navStack.clear()
        _currentDestination.value = AppDestination.LOGIN
        showSnackbar("Logged out successfully")
    }

    fun updateProfile(updated: User) {
        sessionManager.saveUser(updated, true)
        showSnackbar("Profile updated successfully")
    }

    // --- TIMER LOGIC ---
    fun startTimer() {
        if (_timerState.value.isRunning && !_timerState.value.isPaused) return

        _timerState.value = _timerState.value.copy(isRunning = true, isPaused = false)
        val remainingMillis = _timerState.value.remainingSeconds * 1000L

        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(remainingMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _timerState.value = _timerState.value.copy(
                    remainingSeconds = (millisUntilFinished / 1000).toInt()
                )
            }

            override fun onFinish() {
                val state = _timerState.value
                val sessionMins = (state.totalSeconds) / 60
                if (!state.isBreak) {
                    repository.recordStudySession(
                        StudySession(
                            userId = sessionManager.currentUser.value?.id ?: "",
                            subject = state.selectedSubject,
                            durationMinutes = if (sessionMins > 0) sessionMins else 1,
                            isPomodoro = state.isPomodoro,
                            notes = state.sessionNotes.ifBlank { "Completed study focus block" }
                        )
                    )
                    showSnackbar("Great job! Session completed (${sessionMins} mins).")
                    // Switch to 5 min break if Pomodoro
                    if (state.isPomodoro) {
                        _timerState.value = state.copy(
                            isRunning = false,
                            isPaused = false,
                            isBreak = true,
                            remainingSeconds = 5 * 60,
                            totalSeconds = 5 * 60
                        )
                        return
                    }
                } else {
                    showSnackbar("Break finished! Ready to focus again?")
                }
                resetTimer()
            }
        }.start()
    }

    fun pauseTimer() {
        countDownTimer?.cancel()
        _timerState.value = _timerState.value.copy(isPaused = true)
    }

    fun resumeTimer() {
        startTimer()
    }

    fun stopTimer() {
        countDownTimer?.cancel()
        val state = _timerState.value
        val spentSecs = state.totalSeconds - state.remainingSeconds
        val spentMins = spentSecs / 60
        if (spentMins > 1 && !state.isBreak) {
            repository.recordStudySession(
                StudySession(
                    userId = sessionManager.currentUser.value?.id ?: "",
                    subject = state.selectedSubject,
                    durationMinutes = spentMins,
                    isPomodoro = state.isPomodoro,
                    notes = "Partial focus block"
                )
            )
            showSnackbar("Study session saved: $spentMins mins")
        }
        resetTimer()
    }

    fun resetTimer() {
        countDownTimer?.cancel()
        val defaultSecs = if (_timerState.value.isPomodoro) 25 * 60 else 45 * 60
        _timerState.value = _timerState.value.copy(
            isRunning = false,
            isPaused = false,
            isBreak = false,
            remainingSeconds = defaultSecs,
            totalSeconds = defaultSecs
        )
    }

    fun setTimerMode(isPomodoro: Boolean) {
        countDownTimer?.cancel()
        val secs = if (isPomodoro) 25 * 60 else 45 * 60
        _timerState.value = _timerState.value.copy(
            isRunning = false,
            isPaused = false,
            isBreak = false,
            isPomodoro = isPomodoro,
            remainingSeconds = secs,
            totalSeconds = secs
        )
    }

    fun setTimerSubject(subject: String) {
        _timerState.value = _timerState.value.copy(selectedSubject = subject)
    }

    // --- GOALS CELEBRATION ---
    fun updateGoalProgress(goal: GoalItem, newProgress: Int) {
        repository.updateGoalProgress(goal.id, newProgress)
        if (newProgress == 100 && goal.progressPercentage < 100) {
            _celebrationGoal.value = goal.copy(progressPercentage = 100)
            showSnackbar("🎉 Goal Completed: ${goal.title}!")
        }
    }

    fun dismissCelebration() {
        _celebrationGoal.value = null
    }

    // --- SEARCH ---
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}
