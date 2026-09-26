package com.example.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import com.example.viewmodel.AppDestination
import java.util.Calendar

@Composable
fun DashboardScreen(
    currentUser: User?,
    timetable: List<TimetableItem>,
    homework: List<HomeworkItem>,
    exams: List<ExamItem>,
    goals: List<GoalItem>,
    studySessions: List<StudySession>,
    attendance: List<AttendanceItem>,
    announcements: List<Announcement>,
    onNavigate: (AppDestination) -> Unit,
    onQuickActionClick: () -> Unit
) {
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 4..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    val studentName = currentUser?.name ?: "Student"

    // Statistics
    val todayClassesCount = timetable.count { it.dayOfWeek.equals("Monday", ignoreCase = true) }
    val pendingHwCount = homework.count { it.status != HomeworkStatus.COMPLETED }
    val completedHwCount = homework.count { it.status == HomeworkStatus.COMPLETED }
    val upcomingExamsCount = exams.count { !it.isCompleted }
    val totalStudyMinutes = studySessions.sumOf { it.durationMinutes }
    val totalStudyHoursFormatted = String.format("%.1f", totalStudyMinutes / 60.0)
    val overallAttendance = if (attendance.isNotEmpty()) {
        val total = attendance.sumOf { it.totalClasses }
        val attended = attendance.sumOf { it.attendedClasses }
        if (total > 0) (attended * 100) / total else 100
    } else 100
    val completedGoalsCount = goals.count { it.progressPercentage == 100 }
    val totalGoalsCount = goals.size
    val overallProgressPercent = if (totalGoalsCount > 0) {
        goals.sumOf { it.progressPercentage } / totalGoalsCount
    } else 0

    val nextExam = exams.firstOrNull { !it.isCompleted }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_scroll_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Welcome Card with Gradient
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(StudyMateGradient)
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = studentName.take(1).uppercase(),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "$greeting,",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                                Text(
                                    text = studentName,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "“Plan Your Study. Track Your Progress. Achieve Your Goals.”",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick action launcher strip
                        FilledTonalButton(
                            onClick = onQuickActionClick,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color.White,
                                contentColor = StudyMateBlue
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("dashboard_quick_actions_button")
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Quick Action Menu", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Pinned Announcement Banner (if available)
        announcements.firstOrNull { it.isPinned }?.let { ann ->
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Announcement",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = ann.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = ann.content,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }

        // 8 Core Dashboard Metric Cards (2-column layout)
        item {
            Text(
                text = "Academic Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Today's Classes",
                        value = "$todayClassesCount",
                        subtext = "Monday schedule",
                        icon = Icons.Default.CalendarMonth,
                        accentColor = Color(0xFF2563EB),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(AppDestination.TIMETABLE) }
                    )
                    MetricCard(
                        title = "Today's Tasks",
                        value = "${homework.size}",
                        subtext = "$completedHwCount completed",
                        icon = Icons.Default.TaskAlt,
                        accentColor = Color(0xFF7C3AED),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(AppDestination.HOMEWORK) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Pending Homework",
                        value = "$pendingHwCount",
                        subtext = "Action required",
                        icon = Icons.Default.AssignmentLate,
                        accentColor = Color(0xFFEA580C),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(AppDestination.HOMEWORK) }
                    )
                    MetricCard(
                        title = "Upcoming Exams",
                        value = "$upcomingExamsCount",
                        subtext = if (nextExam != null) "Next: ${nextExam.subject}" else "All clear",
                        icon = Icons.Default.School,
                        accentColor = Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(AppDestination.EXAMS) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Study Hours",
                        value = "$totalStudyHoursFormatted h",
                        subtext = "${studySessions.size} focus sessions",
                        icon = Icons.Default.Timer,
                        accentColor = Color(0xFF059669),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(AppDestination.TIMER) }
                    )
                    MetricCard(
                        title = "Attendance",
                        value = "$overallAttendance%",
                        subtext = if (overallAttendance >= 75) "On track" else "Attention needed",
                        icon = Icons.Default.FactCheck,
                        accentColor = if (overallAttendance >= 75) Color(0xFF10B981) else Color(0xFFEF4444),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(AppDestination.ATTENDANCE) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Completed Goals",
                        value = "$completedGoalsCount / $totalGoalsCount",
                        subtext = "Milestones hit",
                        icon = Icons.Default.EmojiEvents,
                        accentColor = Color(0xFF8B5CF6),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(AppDestination.GOALS) }
                    )
                    MetricCard(
                        title = "Progress Rate",
                        value = "$overallProgressPercent%",
                        subtext = "Average target",
                        icon = Icons.Default.TrendingUp,
                        accentColor = Color(0xFF06B6D4),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(AppDestination.PROGRESS) }
                    )
                }
            }
        }

        // Upcoming Exam Countdown Highlight
        if (nextExam != null) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate(AppDestination.EXAMS) }
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = Color(0xFFF59E0B)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Exam Countdown",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFEF3C7)
                            ) {
                                Text(
                                    text = "10 Days Left",
                                    color = Color(0xFFB45309),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = nextExam.examName,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "${nextExam.subject} • ${nextExam.examDate} at ${nextExam.examTime}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Location: ${nextExam.examLocation}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Today's Timetable Preview
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Schedule (Monday)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { onNavigate(AppDestination.TIMETABLE) }) {
                    Text("View Full")
                }
            }
        }

        item {
            val todayClasses = timetable.filter { it.dayOfWeek.equals("Monday", ignoreCase = true) }
            if (todayClasses.isEmpty()) {
                Text(
                    text = "No classes scheduled for today! Enjoy your self-study time.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    todayClasses.forEach { item ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(android.graphics.Color.parseColor(item.colorHex))
                                ) {
                                    Box(modifier = Modifier.size(width = 4.dp, height = 36.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.subject, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(
                                        "${item.teacherName} • ${item.room}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface
                                ) {
                                    Text(
                                        "${item.startTime} - ${item.endTime}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Subject-wise Study Progress Preview
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Subject-Wise Progress",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { onNavigate(AppDestination.PROGRESS) }) {
                    Text("Details")
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SubjectProgressRow("Mathematics", 0.75f, Color(0xFF2563EB))
                    SubjectProgressRow("Computer Science", 0.85f, Color(0xFF7C3AED))
                    SubjectProgressRow("Physics", 0.60f, Color(0xFF059669))
                    SubjectProgressRow("English Literature", 0.90f, Color(0xFFEA580C))
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtext: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = subtext,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun SubjectProgressRow(subject: String, progress: Float, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(subject, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text("${(progress * 100).toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}
