package com.example.ui.screens.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*

@Composable
fun ProgressTrackerScreen(
    studySessions: List<StudySession>,
    homework: List<HomeworkItem>,
    goals: List<GoalItem>,
    exams: List<ExamItem>
) {
    val totalMins = studySessions.sumOf { it.durationMinutes }
    val totalHours = String.format("%.1f", totalMins / 60.0)

    val completedHw = homework.count { it.status == HomeworkStatus.COMPLETED }
    val totalHw = homework.size

    val completedGoals = goals.count { it.progressPercentage == 100 }
    val totalGoals = goals.size

    val completedExams = exams.count { it.isCompleted }
    val totalExams = exams.size

    // Mock weekly bar distribution (Mon-Sun in hours)
    val weeklyDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val weeklyHours = listOf(2.5f, 3.2f, 1.8f, 4.0f, 2.0f, 3.5f, 1.5f)
    val maxBarHour = weeklyHours.maxOrNull() ?: 4f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("progress_screen_scroll"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Study Stats Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Total Focus Time",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "$totalHours Hours",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatPill("Daily Avg", "2.6 h")
                        StatPill("Weekly Total", "18.5 h")
                        StatPill("Monthly Est", "74.0 h")
                    }
                }
            }
        }

        // Weekly Study Hours Bar Chart (Canvas)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Weekly Study Hours",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Monday to Sunday breakdown",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    val primaryColor = MaterialTheme.colorScheme.primary

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    ) {
                        val barWidth = 28.dp.toPx()
                        val spacing = (size.width - (barWidth * weeklyHours.size)) / (weeklyHours.size + 1)
                        val chartHeight = size.height - 30.dp.toPx()

                        weeklyHours.forEachIndexed { index, hours ->
                            val x = spacing + index * (barWidth + spacing)
                            val barHeight = (hours / maxBarHour) * chartHeight
                            val y = chartHeight - barHeight

                            // Background bar trace
                            drawRoundRect(
                                color = primaryColor.copy(alpha = 0.12f),
                                topLeft = Offset(x, 0f),
                                size = Size(barWidth, chartHeight),
                                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                            )

                            // Actual bar value
                            drawRoundRect(
                                color = primaryColor,
                                topLeft = Offset(x, y),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                            )
                        }
                    }

                    // Days labels
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        weeklyDays.forEach { day ->
                            Text(
                                text = day,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Subject-Wise Progress Breakdown
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Subject-Wise Mastery",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    ProgressBarItem("Mathematics", 75, Color(0xFF2563EB))
                    ProgressBarItem("Computer Science", 85, Color(0xFF7C3AED))
                    ProgressBarItem("Physics", 60, Color(0xFF059669))
                    ProgressBarItem("English Literature", 90, Color(0xFFEA580C))
                    ProgressBarItem("Chemistry", 68, Color(0xFF06B6D4))
                }
            }
        }

        // Academic Completion Summary Grid
        item {
            Text(
                text = "Milestones Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryBox(
                    label = "Homework Done",
                    value = "$completedHw / $totalHw",
                    percentage = if (totalHw > 0) (completedHw * 100) / totalHw else 100,
                    color = Color(0xFF10B981),
                    modifier = Modifier.weight(1f)
                )
                SummaryBox(
                    label = "Goals Reached",
                    value = "$completedGoals / $totalGoals",
                    percentage = if (totalGoals > 0) (completedGoals * 100) / totalGoals else 100,
                    color = Color(0xFF8B5CF6),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryBox(
                    label = "Exams Cleared",
                    value = "$completedExams / $totalExams",
                    percentage = if (totalExams > 0) (completedExams * 100) / totalExams else 0,
                    color = Color(0xFFF59E0B),
                    modifier = Modifier.weight(1f)
                )
                SummaryBox(
                    label = "Study Logs",
                    value = "${studySessions.size} Sessions",
                    percentage = 88,
                    color = Color(0xFF06B6D4),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatPill(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun ProgressBarItem(subject: String, percent: Int, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(subject, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text("$percent%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { percent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}

@Composable
private fun SummaryBox(label: String, value: String, percentage: Int, color: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { percentage / 100f },
                color = color,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
        }
    }
}
