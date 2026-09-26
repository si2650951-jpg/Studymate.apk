package com.example.model

enum class UserRole {
    STUDENT,
    ADMIN
}

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: UserRole = UserRole.STUDENT,
    val className: String = "",
    val schoolName: String = "",
    val profileImage: String = "",
    val bio: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class Priority {
    LOW,
    MEDIUM,
    HIGH
}

enum class HomeworkStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED
}

data class CourseItem(
    val id: String = "",
    val name: String = "",
    val code: String = "",
    val instructor: String = "",
    val colorHex: String = "#2563EB",
    val description: String = ""
)

data class TimetableItem(
    val id: String = "",
    val userId: String = "",
    val subject: String = "",
    val teacherName: String = "",
    val room: String = "",
    val dayOfWeek: String = "Monday", // Monday to Sunday
    val startTime: String = "09:00",
    val endTime: String = "10:00",
    val notes: String = "",
    val colorHex: String = "#3B82F6"
)

data class NoteItem(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val subject: String = "",
    val description: String = "",
    val tags: List<String> = emptyList(),
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val attachmentUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class HomeworkItem(
    val id: String = "",
    val userId: String = "",
    val subject: String = "",
    val title: String = "",
    val description: String = "",
    val dueDate: String = "", // e.g. "2026-09-30"
    val priority: Priority = Priority.MEDIUM,
    val status: HomeworkStatus = HomeworkStatus.PENDING,
    val attachmentUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class ExamItem(
    val id: String = "",
    val userId: String = "",
    val examName: String = "",
    val subject: String = "",
    val examDate: String = "", // e.g. "2026-10-05"
    val examTime: String = "10:00 AM",
    val examLocation: String = "Hall A",
    val syllabus: String = "",
    val notes: String = "",
    val isCompleted: Boolean = false
)

data class GoalItem(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val subject: String = "",
    val targetDate: String = "",
    val priority: Priority = Priority.HIGH,
    val progressPercentage: Int = 0 // 0, 25, 50, 75, 100
)

data class StudySession(
    val id: String = "",
    val userId: String = "",
    val subject: String = "",
    val durationMinutes: Int = 25,
    val date: Long = System.currentTimeMillis(),
    val isPomodoro: Boolean = true,
    val notes: String = ""
)

data class AttendanceItem(
    val id: String = "",
    val userId: String = "",
    val subject: String = "",
    val totalClasses: Int = 0,
    val attendedClasses: Int = 0,
    val absentClasses: Int = 0,
    val minPercentageAlert: Int = 75
) {
    val percentage: Int
        get() = if (totalClasses > 0) ((attendedClasses * 100) / totalClasses) else 100
}

data class Announcement(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val authorName: String = "StudyMate Admin",
    val date: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false
)

data class SupportTicket(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val subject: String = "",
    val message: String = "",
    val date: Long = System.currentTimeMillis(),
    val status: String = "Open"
)

data class NotificationItem(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "GENERAL", // CLASS, HOMEWORK, EXAM, GOAL, ATTENDANCE
    val isRead: Boolean = false
)
