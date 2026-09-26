package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.screens.admin.AdminPanelScreen
import com.example.ui.screens.attendance.*
import com.example.ui.screens.auth.LoginScreen
import com.example.ui.screens.auth.SignUpScreen
import com.example.ui.screens.calendar.StudyCalendarScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.exams.*
import com.example.ui.screens.goals.GoalsScreen
import com.example.ui.screens.homework.*
import com.example.ui.screens.info.*
import com.example.ui.screens.notes.*
import com.example.ui.screens.notifications.NotificationsScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.progress.ProgressTrackerScreen
import com.example.ui.screens.search.GlobalSearchScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.timetable.*
import com.example.ui.screens.timer.StudyTimerScreen
import com.example.ui.theme.StudyMateTheme
import com.example.viewmodel.AppDestination
import com.example.viewmodel.StudyMateViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: StudyMateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudyMateApp(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyMateApp(viewModel: StudyMateViewModel) {
    val currentDestination by viewModel.currentDestination.collectAsStateWithLifecycle()
    val currentUser by viewModel.sessionManager.currentUser.collectAsStateWithLifecycle()
    val themeMode by viewModel.sessionManager.themeMode.collectAsStateWithLifecycle()
    val language by viewModel.sessionManager.language.collectAsStateWithLifecycle()
    val notificationPrefs by viewModel.sessionManager.notificationPrefs.collectAsStateWithLifecycle()

    val timetable by viewModel.repository.timetable.collectAsStateWithLifecycle()
    val courses by viewModel.repository.courses.collectAsStateWithLifecycle()
    val notes by viewModel.repository.notes.collectAsStateWithLifecycle()
    val homework by viewModel.repository.homework.collectAsStateWithLifecycle()
    val exams by viewModel.repository.exams.collectAsStateWithLifecycle()
    val goals by viewModel.repository.goals.collectAsStateWithLifecycle()
    val studySessions by viewModel.repository.studySessions.collectAsStateWithLifecycle()
    val attendance by viewModel.repository.attendance.collectAsStateWithLifecycle()
    val announcements by viewModel.repository.announcements.collectAsStateWithLifecycle()
    val notifications by viewModel.repository.notifications.collectAsStateWithLifecycle()
    val registeredStudents by viewModel.repository.registeredStudents.collectAsStateWithLifecycle()

    val timerState by viewModel.timerState.collectAsStateWithLifecycle()
    val celebrationGoal by viewModel.celebrationGoal.collectAsStateWithLifecycle()
    val snackbarMsg by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showQuickActionSheet by remember { mutableStateOf(false) }

    // Quick add dialog states
    var showQuickAddTaskDialog by remember { mutableStateOf(false) }
    var showQuickAddNoteDialog by remember { mutableStateOf(false) }
    var showQuickAddTimetableDialog by remember { mutableStateOf(false) }
    var showQuickAddExamDialog by remember { mutableStateOf(false) }

    // Display snackbars
    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // Handle Android system Back Press
    BackHandler(enabled = currentDestination != AppDestination.SPLASH) {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else {
            val handled = viewModel.navigateBack()
            if (!handled && currentDestination == AppDestination.DASHBOARD) {
                // At root of authenticated app
            }
        }
    }

    val isAuthScreen = currentDestination == AppDestination.SPLASH ||
            currentDestination == AppDestination.LOGIN ||
            currentDestination == AppDestination.SIGN_UP

    val showBottomBar = !isAuthScreen && (
            currentDestination == AppDestination.DASHBOARD ||
                    currentDestination == AppDestination.TIMETABLE ||
                    currentDestination == AppDestination.HOMEWORK ||
                    currentDestination == AppDestination.NOTES ||
                    currentDestination == AppDestination.PROFILE
            )

    StudyMateTheme(themeMode = themeMode) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = !isAuthScreen,
            drawerContent = {
                StudyMateDrawerContent(
                    currentUser = currentUser,
                    currentDestination = currentDestination,
                    onNavigate = { dest ->
                        coroutineScope.launch { drawerState.close() }
                        viewModel.navigateTo(dest)
                    },
                    onLogout = {
                        coroutineScope.launch { drawerState.close() }
                        viewModel.logout()
                    }
                )
            }
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    if (!isAuthScreen) {
                        StudyMateTopAppBar(
                            title = currentDestination.label,
                            unreadNotificationCount = notifications.size,
                            onMenuClick = {
                                coroutineScope.launch {
                                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                }
                            },
                            onSearchClick = { viewModel.navigateTo(AppDestination.GLOBAL_SEARCH) },
                            onNotificationsClick = { viewModel.navigateTo(AppDestination.NOTIFICATIONS) },
                            onSettingsClick = { viewModel.navigateTo(AppDestination.SETTINGS) }
                        )
                    }
                },
                bottomBar = {
                    if (showBottomBar) {
                        StudyMateBottomBar(
                            currentDestination = currentDestination,
                            onNavigate = { dest -> viewModel.navigateTo(dest) }
                        )
                    }
                },
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentDestination) {
                        AppDestination.SPLASH -> {
                            SplashScreen()
                        }
                        AppDestination.LOGIN -> {
                            LoginScreen(
                                onLoginClick = { email, pass, rememberMe, onError ->
                                    viewModel.login(email, pass, rememberMe, onSuccess = {}, onError = onError)
                                },
                                onGoogleLoginClick = {
                                    viewModel.googleSignIn(onSuccess = {})
                                },
                                onNavigateToSignUp = { viewModel.navigateTo(AppDestination.SIGN_UP) }
                            )
                        }
                        AppDestination.SIGN_UP -> {
                            SignUpScreen(
                                onSignUpClick = { name, email, phone, pass, confirmPass, className, schoolName, onError ->
                                    viewModel.signUp(name, email, phone, pass, confirmPass, className, schoolName, onSuccess = {}, onError = onError)
                                },
                                onNavigateToLogin = { viewModel.navigateTo(AppDestination.LOGIN) }
                            )
                        }
                        AppDestination.DASHBOARD -> {
                            DashboardScreen(
                                currentUser = currentUser,
                                timetable = timetable,
                                homework = homework,
                                exams = exams,
                                goals = goals,
                                studySessions = studySessions,
                                attendance = attendance,
                                announcements = announcements,
                                onNavigate = { dest -> viewModel.navigateTo(dest) },
                                onQuickActionClick = { showQuickActionSheet = true }
                            )
                        }
                        AppDestination.TIMETABLE -> {
                            TimetableScreen(
                                timetable = timetable,
                                onAddItem = { viewModel.repository.addTimetableItem(it) },
                                onUpdateItem = { viewModel.repository.updateTimetableItem(it) },
                                onDeleteItem = { viewModel.repository.deleteTimetableItem(it) }
                            )
                        }
                        AppDestination.NOTES -> {
                            NotesScreen(
                                notes = notes,
                                courses = courses,
                                onAddCourse = { viewModel.repository.addCourse(it) },
                                onAddNote = { viewModel.repository.addNote(it) },
                                onUpdateNote = { viewModel.repository.updateNote(it) },
                                onTogglePin = { viewModel.repository.toggleNotePin(it) },
                                onToggleFavorite = { viewModel.repository.toggleNoteFavorite(it) },
                                onDeleteNote = { viewModel.repository.deleteNote(it) }
                            )
                        }
                        AppDestination.HOMEWORK -> {
                            HomeworkScreen(
                                homeworkList = homework,
                                onAddHomework = { viewModel.repository.addHomework(it) },
                                onUpdateHomework = { viewModel.repository.updateHomework(it) },
                                onUpdateStatus = { id, status -> viewModel.repository.updateHomeworkStatus(id, status) },
                                onDeleteHomework = { viewModel.repository.deleteHomework(it) }
                            )
                        }
                        AppDestination.EXAMS -> {
                            ExamScreen(
                                exams = exams,
                                onAddExam = { viewModel.repository.addExam(it) },
                                onUpdateExam = { viewModel.repository.updateExam(it) },
                                onDeleteExam = { viewModel.repository.deleteExam(it) }
                            )
                        }
                        AppDestination.GOALS -> {
                            GoalsScreen(
                                goals = goals,
                                onAddGoal = { viewModel.repository.addGoal(it) },
                                onUpdateGoal = { viewModel.repository.updateGoal(it) },
                                onUpdateProgress = { goal, progress ->
                                    viewModel.updateGoalProgress(goal, progress)
                                },
                                onDeleteGoal = { viewModel.repository.deleteGoal(it) }
                            )
                        }
                        AppDestination.TIMER -> {
                            StudyTimerScreen(
                                timerState = timerState,
                                sessions = studySessions,
                                onStart = { viewModel.startTimer() },
                                onPause = { viewModel.pauseTimer() },
                                onResume = { viewModel.resumeTimer() },
                                onStop = { viewModel.stopTimer() },
                                onReset = { viewModel.resetTimer() },
                                onSetMode = { viewModel.setTimerMode(it) },
                                onSetSubject = { viewModel.setTimerSubject(it) }
                            )
                        }
                        AppDestination.PROGRESS -> {
                            ProgressTrackerScreen(
                                studySessions = studySessions,
                                homework = homework,
                                goals = goals,
                                exams = exams
                            )
                        }
                        AppDestination.ATTENDANCE -> {
                            AttendanceScreen(
                                attendanceList = attendance,
                                onAddAttendance = { viewModel.repository.addAttendance(it) },
                                onUpdateAttendance = { viewModel.repository.updateAttendance(it) },
                                onMarkAttendance = { id, present -> viewModel.repository.markAttendance(id, present) },
                                onDeleteAttendance = { viewModel.repository.deleteAttendance(it) }
                            )
                        }
                        AppDestination.CALENDAR -> {
                            StudyCalendarScreen(
                                timetable = timetable,
                                homework = homework,
                                exams = exams,
                                goals = goals,
                                studySessions = studySessions
                            )
                        }
                        AppDestination.NOTIFICATIONS -> {
                            NotificationsScreen(
                                notifications = notifications,
                                preferences = notificationPrefs,
                                onDismissNotification = { viewModel.repository.dismissNotification(it) },
                                onClearAll = { viewModel.repository.clearAllNotifications() },
                                onUpdatePreferences = { viewModel.sessionManager.updateNotificationPreferences(it) }
                            )
                        }
                        AppDestination.GLOBAL_SEARCH -> {
                            GlobalSearchScreen(
                                notes = notes,
                                homework = homework,
                                exams = exams,
                                goals = goals,
                                timetable = timetable,
                                onNavigate = { dest -> viewModel.navigateTo(dest) }
                            )
                        }
                        AppDestination.PROFILE -> {
                            ProfileScreen(
                                currentUser = currentUser,
                                onUpdateProfile = { viewModel.updateProfile(it) },
                                onNavigate = { dest -> viewModel.navigateTo(dest) },
                                onLogout = { viewModel.logout() }
                            )
                        }
                        AppDestination.ADMIN_PANEL -> {
                            AdminPanelScreen(
                                students = registeredStudents,
                                announcements = announcements,
                                totalNotes = notes.size,
                                totalHomework = homework.size,
                                totalExams = exams.size,
                                totalStudySessions = studySessions.size,
                                onAddAnnouncement = { viewModel.repository.addAnnouncement(it) },
                                onDeleteAnnouncement = { viewModel.repository.deleteAnnouncement(it) },
                                onRemoveStudent = { viewModel.repository.removeStudent(it) }
                            )
                        }
                        AppDestination.SETTINGS -> {
                            SettingsScreen(
                                currentTheme = themeMode,
                                currentLanguage = language,
                                notificationPrefs = notificationPrefs,
                                onSelectTheme = { viewModel.sessionManager.setThemeMode(it) },
                                onSelectLanguage = { viewModel.sessionManager.setLanguage(it) },
                                onUpdateNotifications = { viewModel.sessionManager.updateNotificationPreferences(it) },
                                onNavigate = { dest -> viewModel.navigateTo(dest) },
                                onLogout = { viewModel.logout() }
                            )
                        }
                        AppDestination.ABOUT -> {
                            AboutScreen()
                        }
                        AppDestination.PRIVACY_POLICY -> {
                            PrivacyPolicyScreen()
                        }
                        AppDestination.TERMS -> {
                            TermsScreen()
                        }
                        AppDestination.HELP_SUPPORT -> {
                            HelpSupportScreen(
                                onSubmitTicket = { viewModel.repository.submitSupportTicket(it) }
                            )
                        }
                    }
                }
            }
        }

        // Quick Action Modal Sheet
        if (showQuickActionSheet) {
            QuickActionBottomSheet(
                onDismiss = { showQuickActionSheet = false },
                onAddTask = { showQuickAddTaskDialog = true },
                onAddNote = { showQuickAddNoteDialog = true },
                onAddTimetable = { showQuickAddTimetableDialog = true },
                onAddExam = { showQuickAddExamDialog = true },
                onStartTimer = {
                    viewModel.navigateTo(AppDestination.TIMER)
                    viewModel.startTimer()
                }
            )
        }

        // Quick Add Task Dialog
        if (showQuickAddTaskDialog) {
            AddEditHomeworkDialog(
                initialItem = null,
                onDismiss = { showQuickAddTaskDialog = false },
                onSave = {
                    viewModel.repository.addHomework(it)
                    showQuickAddTaskDialog = false
                    viewModel.showSnackbar("Task added: ${it.title}")
                }
            )
        }

        // Quick Add Note Dialog
        if (showQuickAddNoteDialog) {
            AddEditNoteDialog(
                initialNote = null,
                courses = courses,
                onDismiss = { showQuickAddNoteDialog = false },
                onSave = {
                    viewModel.repository.addNote(it)
                    showQuickAddNoteDialog = false
                    viewModel.showSnackbar("Note created: ${it.title}")
                }
            )
        }

        // Quick Add Timetable Dialog
        if (showQuickAddTimetableDialog) {
            AddEditTimetableDialog(
                initialItem = null,
                defaultDay = "Monday",
                onDismiss = { showQuickAddTimetableDialog = false },
                onSave = {
                    viewModel.repository.addTimetableItem(it)
                    showQuickAddTimetableDialog = false
                    viewModel.showSnackbar("Class added: ${it.subject}")
                }
            )
        }

        // Quick Add Exam Dialog
        if (showQuickAddExamDialog) {
            AddEditExamDialog(
                initialExam = null,
                onDismiss = { showQuickAddExamDialog = false },
                onSave = {
                    viewModel.repository.addExam(it)
                    showQuickAddExamDialog = false
                    viewModel.showSnackbar("Exam scheduled: ${it.examName}")
                }
            )
        }

        // Goal Reached 100% Celebration Dialog
        celebrationGoal?.let { goal ->
            CelebrationDialog(
                goal = goal,
                onDismiss = { viewModel.dismissCelebration() }
            )
        }
    }
}
