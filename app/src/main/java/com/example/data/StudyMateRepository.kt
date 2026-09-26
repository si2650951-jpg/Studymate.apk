package com.example.data

import android.content.Context
import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class StudyMateRepository(
    private val context: Context,
    private val sessionManager: SessionManager
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val firestore = FirebaseHelper.getFirestore(context)

    // In-memory reactive state
    private val _timetable = MutableStateFlow<List<TimetableItem>>(emptyList())
    val timetable: StateFlow<List<TimetableItem>> = _timetable.asStateFlow()

    private val _courses = MutableStateFlow<List<CourseItem>>(emptyList())
    val courses: StateFlow<List<CourseItem>> = _courses.asStateFlow()

    private val _notes = MutableStateFlow<List<NoteItem>>(emptyList())
    val notes: StateFlow<List<NoteItem>> = _notes.asStateFlow()

    private val _homework = MutableStateFlow<List<HomeworkItem>>(emptyList())
    val homework: StateFlow<List<HomeworkItem>> = _homework.asStateFlow()

    private val _exams = MutableStateFlow<List<ExamItem>>(emptyList())
    val exams: StateFlow<List<ExamItem>> = _exams.asStateFlow()

    private val _goals = MutableStateFlow<List<GoalItem>>(emptyList())
    val goals: StateFlow<List<GoalItem>> = _goals.asStateFlow()

    private val _studySessions = MutableStateFlow<List<StudySession>>(emptyList())
    val studySessions: StateFlow<List<StudySession>> = _studySessions.asStateFlow()

    private val _attendance = MutableStateFlow<List<AttendanceItem>>(emptyList())
    val attendance: StateFlow<List<AttendanceItem>> = _attendance.asStateFlow()

    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements: StateFlow<List<Announcement>> = _announcements.asStateFlow()

    private val _supportTickets = MutableStateFlow<List<SupportTicket>>(emptyList())
    val supportTickets: StateFlow<List<SupportTicket>> = _supportTickets.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _registeredStudents = MutableStateFlow<List<User>>(emptyList())
    val registeredStudents: StateFlow<List<User>> = _registeredStudents.asStateFlow()

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        val defaultUserId = sessionManager.currentUser.value?.id ?: "student_default"

        _timetable.value = listOf(
            TimetableItem(
                id = "tt_1",
                userId = defaultUserId,
                subject = "Mathematics",
                teacherName = "Prof. Robert Smith",
                room = "Room 302",
                dayOfWeek = "Monday",
                startTime = "09:00",
                endTime = "10:15",
                notes = "Calculus chapter 4 problem set review",
                colorHex = "#2563EB"
            ),
            TimetableItem(
                id = "tt_2",
                userId = defaultUserId,
                subject = "Computer Science",
                teacherName = "Dr. Alan Turing",
                room = "Lab 105",
                dayOfWeek = "Monday",
                startTime = "10:30",
                endTime = "12:00",
                notes = "Data Structures & Algorithms in Kotlin",
                colorHex = "#7C3AED"
            ),
            TimetableItem(
                id = "tt_3",
                userId = defaultUserId,
                subject = "Physics",
                teacherName = "Prof. Marie Curie",
                room = "Hall B",
                dayOfWeek = "Tuesday",
                startTime = "11:00",
                endTime = "12:30",
                notes = "Thermodynamics and electromagnetism lab",
                colorHex = "#059669"
            ),
            TimetableItem(
                id = "tt_4",
                userId = defaultUserId,
                subject = "English Literature",
                teacherName = "Ms. Emily Bronte",
                room = "Room 108",
                dayOfWeek = "Wednesday",
                startTime = "14:00",
                endTime = "15:30",
                notes = "Essay writing and modern drama analysis",
                colorHex = "#EA580C"
            ),
            TimetableItem(
                id = "tt_5",
                userId = defaultUserId,
                subject = "Chemistry",
                teacherName = "Dr. Walter White",
                room = "Chem Lab 2",
                dayOfWeek = "Thursday",
                startTime = "10:00",
                endTime = "11:30",
                notes = "Organic chemistry reaction mechanisms",
                colorHex = "#06B6D4"
            )
        )

        _courses.value = listOf(
            CourseItem(
                id = "course_1",
                name = "Mathematics",
                code = "MATH 101",
                instructor = "Prof. Robert Smith",
                colorHex = "#2563EB",
                description = "Calculus, linear algebra, and discrete mathematical foundations."
            ),
            CourseItem(
                id = "course_2",
                name = "Computer Science",
                code = "CS 201",
                instructor = "Dr. Alan Turing",
                colorHex = "#7C3AED",
                description = "Data structures, algorithms, object-oriented design & Kotlin."
            ),
            CourseItem(
                id = "course_3",
                name = "Physics",
                code = "PHYS 102",
                instructor = "Prof. Marie Curie",
                colorHex = "#059669",
                description = "Thermodynamics, mechanics, waves and electromagnetism."
            ),
            CourseItem(
                id = "course_4",
                name = "English Literature",
                code = "ENG 105",
                instructor = "Ms. Emily Bronte",
                colorHex = "#EA580C",
                description = "Literary analysis, modern drama, academic writing and essays."
            ),
            CourseItem(
                id = "course_5",
                name = "Chemistry",
                code = "CHEM 103",
                instructor = "Dr. Walter White",
                colorHex = "#06B6D4",
                description = "Organic reaction mechanisms, stoichiometry, and laboratory techniques."
            ),
            CourseItem(
                id = "course_6",
                name = "Biology",
                code = "BIO 104",
                instructor = "Dr. Jane Goodall",
                colorHex = "#10B981",
                description = "Cell biology, genetics, ecology, and human physiology."
            )
        )

        _notes.value = listOf(
            NoteItem(
                id = "note_1",
                userId = defaultUserId,
                title = "Calculus Integration Formulas",
                subject = "Mathematics",
                description = "Key substitution rules, integration by parts formulas: \u222B u dv = uv - \u222B v du. Remember trig substitution triangles.",
                tags = listOf("Calculus", "Formulas", "Exam Prep"),
                isPinned = true,
                isFavorite = true,
                attachmentUrl = ""
            ),
            NoteItem(
                id = "note_2",
                userId = defaultUserId,
                title = "Binary Search & Sorting Complexities",
                subject = "Computer Science",
                description = "QuickSort avg O(n log n), worst O(n^2). MergeSort stable O(n log n). Binary search prerequisite: sorted array.",
                tags = listOf("Algorithms", "DataStructures", "CS"),
                isPinned = true,
                isFavorite = false,
                attachmentUrl = ""
            ),
            NoteItem(
                id = "note_3",
                userId = defaultUserId,
                title = "Laws of Thermodynamics Summary",
                subject = "Physics",
                description = "0th: Thermal equilibrium. 1st: Energy conservation \u0394U = Q - W. 2nd: Entropy of isolated systems always increases.",
                tags = listOf("Physics", "Laws", "Revision"),
                isPinned = false,
                isFavorite = true,
                attachmentUrl = ""
            )
        )

        _homework.value = listOf(
            HomeworkItem(
                id = "hw_1",
                userId = defaultUserId,
                subject = "Mathematics",
                title = "Calculus Problem Set 6",
                description = "Complete problems 14 through 28 on page 142. Show all step-by-step working.",
                dueDate = getRelativeDateString(1),
                priority = Priority.HIGH,
                status = HomeworkStatus.PENDING
            ),
            HomeworkItem(
                id = "hw_2",
                userId = defaultUserId,
                subject = "Computer Science",
                title = "Implement Balanced Binary Tree",
                description = "Implement AVL tree insertion and self-balancing rotations in Kotlin or Java.",
                dueDate = getRelativeDateString(3),
                priority = Priority.MEDIUM,
                status = HomeworkStatus.IN_PROGRESS
            ),
            HomeworkItem(
                id = "hw_3",
                userId = defaultUserId,
                subject = "Physics",
                title = "Heat Engine Lab Report",
                description = "Write a 3-page experimental report analyzing Carnot cycle efficiency and errors.",
                dueDate = getRelativeDateString(5),
                priority = Priority.LOW,
                status = HomeworkStatus.PENDING
            ),
            HomeworkItem(
                id = "hw_4",
                userId = defaultUserId,
                subject = "English Literature",
                title = "Essay on Shakespeare's Hamlet",
                description = "500-word critical essay discussing fate vs choice in Hamlet Act 3.",
                dueDate = getRelativeDateString(-1),
                priority = Priority.MEDIUM,
                status = HomeworkStatus.COMPLETED
            )
        )

        _exams.value = listOf(
            ExamItem(
                id = "ex_1",
                userId = defaultUserId,
                examName = "Midterm Calculus Examination",
                subject = "Mathematics",
                examDate = getRelativeDateString(10),
                examTime = "09:30 AM",
                examLocation = "Main Examination Hall A",
                syllabus = "Chapters 1 to 5: Limits, Derivatives, Integration, and Applications.",
                notes = "Calculators permitted (non-graphing). Bring student ID card."
            ),
            ExamItem(
                id = "ex_2",
                userId = defaultUserId,
                examName = "Data Structures Practical Exam",
                subject = "Computer Science",
                examDate = getRelativeDateString(16),
                examTime = "02:00 PM",
                examLocation = "Computer Center Lab 3",
                syllabus = "Arrays, Linked Lists, Stacks, Queues, Binary Trees & Graphs.",
                notes = "Coding assessment on IDE with automated test runner."
            ),
            ExamItem(
                id = "ex_3",
                userId = defaultUserId,
                examName = "Physics Classical Mechanics Test",
                subject = "Physics",
                examDate = getRelativeDateString(24),
                examTime = "11:00 AM",
                examLocation = "Science Building 204",
                syllabus = "Kinematics, Newton's Laws, Rotational Motion, Gravitation.",
                notes = "Formula sheet provided with exam booklet."
            )
        )

        _goals.value = listOf(
            GoalItem(
                id = "g_1",
                userId = defaultUserId,
                title = "Complete Mathematics Chapter 5 Exercises",
                subject = "Mathematics",
                targetDate = getRelativeDateString(4),
                priority = Priority.HIGH,
                progressPercentage = 75
            ),
            GoalItem(
                id = "g_2",
                userId = defaultUserId,
                title = "Master Binary Search Tree Rotations",
                subject = "Computer Science",
                targetDate = getRelativeDateString(6),
                priority = Priority.MEDIUM,
                progressPercentage = 50
            ),
            GoalItem(
                id = "g_3",
                userId = defaultUserId,
                title = "Review 50 Physics Flashcards",
                subject = "Physics",
                targetDate = getRelativeDateString(2),
                priority = Priority.HIGH,
                progressPercentage = 100
            ),
            GoalItem(
                id = "g_4",
                userId = defaultUserId,
                title = "Draft English Term Paper Outline",
                subject = "English Literature",
                targetDate = getRelativeDateString(7),
                priority = Priority.LOW,
                progressPercentage = 25
            )
        )

        _studySessions.value = listOf(
            StudySession(
                id = "ss_1",
                userId = defaultUserId,
                subject = "Mathematics",
                durationMinutes = 50,
                date = System.currentTimeMillis() - 86400000L,
                isPomodoro = true,
                notes = "Calculus problem sets solved"
            ),
            StudySession(
                id = "ss_2",
                userId = defaultUserId,
                subject = "Computer Science",
                durationMinutes = 75,
                date = System.currentTimeMillis() - 172800000L,
                isPomodoro = true,
                notes = "LeetCode tree challenges"
            ),
            StudySession(
                id = "ss_3",
                userId = defaultUserId,
                subject = "Physics",
                durationMinutes = 45,
                date = System.currentTimeMillis() - 259200000L,
                isPomodoro = false,
                notes = "Thermodynamics chapter reading"
            ),
            StudySession(
                id = "ss_4",
                userId = defaultUserId,
                subject = "English Literature",
                durationMinutes = 30,
                date = System.currentTimeMillis() - 345600000L,
                isPomodoro = true,
                notes = "Hamlet literary analysis"
            )
        )

        _attendance.value = listOf(
            AttendanceItem(
                id = "att_1",
                userId = defaultUserId,
                subject = "Mathematics",
                totalClasses = 24,
                attendedClasses = 21,
                absentClasses = 3,
                minPercentageAlert = 75
            ),
            AttendanceItem(
                id = "att_2",
                userId = defaultUserId,
                subject = "Computer Science",
                totalClasses = 20,
                attendedClasses = 19,
                absentClasses = 1,
                minPercentageAlert = 75
            ),
            AttendanceItem(
                id = "att_3",
                userId = defaultUserId,
                subject = "Physics",
                totalClasses = 18,
                attendedClasses = 13,
                absentClasses = 5,
                minPercentageAlert = 75 // 72% - alerts warning!
            ),
            AttendanceItem(
                id = "att_4",
                userId = defaultUserId,
                subject = "English Literature",
                totalClasses = 16,
                attendedClasses = 15,
                absentClasses = 1,
                minPercentageAlert = 75
            )
        )

        _announcements.value = listOf(
            Announcement(
                id = "ann_1",
                title = "Welcome to StudyMate! 🚀",
                content = "Welcome to your new all-in-one student study companion. Plan your timetable, track homework deadlines, set study timers, and achieve all your academic goals!",
                authorName = "StudyMate Team",
                date = System.currentTimeMillis() - 86400000L,
                isPinned = true
            ),
            Announcement(
                id = "ann_2",
                title = "Midterm Examination Timetable Released 📅",
                content = "The official exam schedule for the upcoming semester has been updated. Please review your exam countdowns and locations under the Exams tab.",
                authorName = "Academic Dean Office",
                date = System.currentTimeMillis() - 43200000L,
                isPinned = false
            )
        )

        _notifications.value = listOf(
            NotificationItem(
                id = "notif_1",
                title = "Upcoming Class: Mathematics",
                message = "Room 302 at 09:00 AM with Prof. Robert Smith",
                timestamp = System.currentTimeMillis(),
                type = "CLASS"
            ),
            NotificationItem(
                id = "notif_2",
                title = "Attendance Alert: Physics (72%)",
                message = "Your attendance in Physics is below the 75% required threshold. Attend the next class to improve!",
                timestamp = System.currentTimeMillis() - 3600000L,
                type = "ATTENDANCE"
            ),
            NotificationItem(
                id = "notif_3",
                title = "Homework Due Tomorrow: Calculus",
                message = "Calculus Problem Set 6 is due tomorrow. Complete it now!",
                timestamp = System.currentTimeMillis() - 7200000L,
                type = "HOMEWORK"
            )
        )

        _registeredStudents.value = listOf(
            User(
                id = "std_101",
                name = "Alex Johnson",
                email = "alex.johnson@student.edu",
                phone = "+1 (555) 234-5678",
                role = UserRole.STUDENT,
                className = "Grade 11 - Science",
                schoolName = "Springfield High School",
                bio = "Aspiring software engineer and physics enthusiast.",
                createdAt = System.currentTimeMillis() - 864000000L
            ),
            User(
                id = "std_102",
                name = "Sophia Miller",
                email = "sophia.m@student.edu",
                phone = "+1 (555) 876-5432",
                role = UserRole.STUDENT,
                className = "Grade 12 - Pre-Med",
                schoolName = "Oakridge International",
                bio = "Focused on biology, chemistry and medical entrance prep.",
                createdAt = System.currentTimeMillis() - 1728000000L
            ),
            User(
                id = "admin_1",
                name = "Admin Officer",
                email = "admin@studymate.com",
                phone = "+1 (555) 000-1111",
                role = UserRole.ADMIN,
                className = "Staff Administration",
                schoolName = "StudyMate Academic Portal",
                bio = "System Administrator.",
                createdAt = System.currentTimeMillis() - 2592000000L
            )
        )
    }

    private fun getRelativeDateString(daysFromNow: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, daysFromNow)
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(calendar.time)
    }

    // --- TIMETABLE OPERATIONS ---
    fun addTimetableItem(item: TimetableItem) {
        val newItem = if (item.id.isEmpty()) item.copy(id = "tt_${System.currentTimeMillis()}") else item
        _timetable.value = _timetable.value + newItem
        syncToFirestore("timetable", newItem.id, newItem)
    }

    fun updateTimetableItem(item: TimetableItem) {
        _timetable.value = _timetable.value.map { if (it.id == item.id) item else it }
        syncToFirestore("timetable", item.id, item)
    }

    fun deleteTimetableItem(id: String) {
        _timetable.value = _timetable.value.filterNot { it.id == id }
        deleteFromFirestore("timetable", id)
    }

    // --- COURSES OPERATIONS ---
    fun addCourse(course: CourseItem) {
        val newCourse = if (course.id.isEmpty()) course.copy(id = "course_${System.currentTimeMillis()}") else course
        _courses.value = _courses.value + newCourse
        syncToFirestore("courses", newCourse.id, newCourse)
    }

    fun updateCourse(course: CourseItem) {
        _courses.value = _courses.value.map { if (it.id == course.id) course else it }
        syncToFirestore("courses", course.id, course)
    }

    fun deleteCourse(id: String) {
        _courses.value = _courses.value.filterNot { it.id == id }
        deleteFromFirestore("courses", id)
    }

    // --- NOTES OPERATIONS ---
    fun addNote(note: NoteItem) {
        val newNote = if (note.id.isEmpty()) note.copy(id = "note_${System.currentTimeMillis()}") else note
        _notes.value = listOf(newNote) + _notes.value
        syncToFirestore("notes", newNote.id, newNote)
    }

    fun updateNote(note: NoteItem) {
        _notes.value = _notes.value.map { if (it.id == note.id) note.copy(updatedAt = System.currentTimeMillis()) else it }
        syncToFirestore("notes", note.id, note)
    }

    fun toggleNotePin(id: String) {
        _notes.value = _notes.value.map {
            if (it.id == id) it.copy(isPinned = !it.isPinned) else it
        }
    }

    fun toggleNoteFavorite(id: String) {
        _notes.value = _notes.value.map {
            if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it
        }
    }

    fun deleteNote(id: String) {
        _notes.value = _notes.value.filterNot { it.id == id }
        deleteFromFirestore("notes", id)
    }

    // --- HOMEWORK OPERATIONS ---
    fun addHomework(item: HomeworkItem) {
        val newItem = if (item.id.isEmpty()) item.copy(id = "hw_${System.currentTimeMillis()}") else item
        _homework.value = listOf(newItem) + _homework.value
        syncToFirestore("homework", newItem.id, newItem)
    }

    fun updateHomework(item: HomeworkItem) {
        _homework.value = _homework.value.map { if (it.id == item.id) item else it }
        syncToFirestore("homework", item.id, item)
    }

    fun updateHomeworkStatus(id: String, status: HomeworkStatus) {
        _homework.value = _homework.value.map {
            if (it.id == id) it.copy(status = status) else it
        }
    }

    fun deleteHomework(id: String) {
        _homework.value = _homework.value.filterNot { it.id == id }
        deleteFromFirestore("homework", id)
    }

    // --- EXAMS OPERATIONS ---
    fun addExam(item: ExamItem) {
        val newItem = if (item.id.isEmpty()) item.copy(id = "ex_${System.currentTimeMillis()}") else item
        _exams.value = _exams.value + newItem
        syncToFirestore("exams", newItem.id, newItem)
    }

    fun updateExam(item: ExamItem) {
        _exams.value = _exams.value.map { if (it.id == item.id) item else it }
        syncToFirestore("exams", item.id, item)
    }

    fun deleteExam(id: String) {
        _exams.value = _exams.value.filterNot { it.id == id }
        deleteFromFirestore("exams", id)
    }

    // --- GOALS OPERATIONS ---
    fun addGoal(item: GoalItem) {
        val newItem = if (item.id.isEmpty()) item.copy(id = "g_${System.currentTimeMillis()}") else item
        _goals.value = _goals.value + newItem
        syncToFirestore("goals", newItem.id, newItem)
    }

    fun updateGoal(item: GoalItem) {
        _goals.value = _goals.value.map { if (it.id == item.id) item else it }
        syncToFirestore("goals", item.id, item)
    }

    fun updateGoalProgress(id: String, percentage: Int) {
        _goals.value = _goals.value.map {
            if (it.id == id) it.copy(progressPercentage = percentage.coerceIn(0, 100)) else it
        }
    }

    fun deleteGoal(id: String) {
        _goals.value = _goals.value.filterNot { it.id == id }
        deleteFromFirestore("goals", id)
    }

    // --- STUDY TIMER & SESSIONS ---
    fun recordStudySession(session: StudySession) {
        val newSession = if (session.id.isEmpty()) session.copy(id = "ss_${System.currentTimeMillis()}") else session
        _studySessions.value = listOf(newSession) + _studySessions.value
        syncToFirestore("study_sessions", newSession.id, newSession)
    }

    // --- ATTENDANCE OPERATIONS ---
    fun addAttendance(item: AttendanceItem) {
        val newItem = if (item.id.isEmpty()) item.copy(id = "att_${System.currentTimeMillis()}") else item
        _attendance.value = _attendance.value + newItem
        syncToFirestore("attendance", newItem.id, newItem)
    }

    fun updateAttendance(item: AttendanceItem) {
        _attendance.value = _attendance.value.map { if (it.id == item.id) item else it }
        syncToFirestore("attendance", item.id, item)
    }

    fun markAttendance(id: String, attended: Boolean) {
        _attendance.value = _attendance.value.map {
            if (it.id == id) {
                if (attended) {
                    it.copy(
                        totalClasses = it.totalClasses + 1,
                        attendedClasses = it.attendedClasses + 1
                    )
                } else {
                    it.copy(
                        totalClasses = it.totalClasses + 1,
                        absentClasses = it.absentClasses + 1
                    )
                }
            } else it
        }
    }

    fun deleteAttendance(id: String) {
        _attendance.value = _attendance.value.filterNot { it.id == id }
        deleteFromFirestore("attendance", id)
    }

    // --- ANNOUNCEMENTS ---
    fun addAnnouncement(announcement: Announcement) {
        val newAnn = if (announcement.id.isEmpty()) announcement.copy(id = "ann_${System.currentTimeMillis()}") else announcement
        _announcements.value = listOf(newAnn) + _announcements.value
        syncToFirestore("announcements", newAnn.id, newAnn)
    }

    fun deleteAnnouncement(id: String) {
        _announcements.value = _announcements.value.filterNot { it.id == id }
        deleteFromFirestore("announcements", id)
    }

    // --- SUPPORT TICKETS ---
    fun submitSupportTicket(ticket: SupportTicket) {
        val newTicket = if (ticket.id.isEmpty()) ticket.copy(id = "ticket_${System.currentTimeMillis()}") else ticket
        _supportTickets.value = listOf(newTicket) + _supportTickets.value
        syncToFirestore("support_tickets", newTicket.id, newTicket)
    }

    // --- ADMIN ACTIONS ---
    fun removeStudent(id: String) {
        _registeredStudents.value = _registeredStudents.value.filterNot { it.id == id }
    }

    fun updateStudent(updated: User) {
        _registeredStudents.value = _registeredStudents.value.map {
            if (it.id == updated.id) updated else it
        }
    }

    // --- NOTIFICATIONS ---
    fun dismissNotification(id: String) {
        _notifications.value = _notifications.value.filterNot { it.id == id }
    }

    fun clearAllNotifications() {
        _notifications.value = emptyList()
    }

    // Firestore helper sync methods
    private fun syncToFirestore(collection: String, docId: String, data: Any) {
        firestore?.let { db ->
            scope.launch {
                try {
                    db.collection(collection).document(docId).set(data)
                } catch (e: Exception) {
                    // Log or handle gracefully
                }
            }
        }
    }

    private fun deleteFromFirestore(collection: String, docId: String) {
        firestore?.let { db ->
            scope.launch {
                try {
                    db.collection(collection).document(docId).delete()
                } catch (e: Exception) {
                    // Handle gracefully
                }
            }
        }
    }
}
