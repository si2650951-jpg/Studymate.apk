package com.example.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StudyCalendarScreen(
    timetable: List<TimetableItem>,
    homework: List<HomeworkItem>,
    exams: List<ExamItem>,
    goals: List<GoalItem>,
    studySessions: List<StudySession>
) {
    val calendar = remember { Calendar.getInstance() }
    var selectedDayNumber by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }

    val monthName = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time) }
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    val weekHeader = listOf("S", "M", "T", "W", "T", "F", "S")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("calendar_screen_scroll"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Month Title Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = monthName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row {
                            IconButton(onClick = {}) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
                            }
                            IconButton(onClick = {}) {
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Days of week header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        weekHeader.forEach { d ->
                            Text(
                                text = d,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Simple calendar grid for current month
                    val daysList = (1..daysInMonth).toList()
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        daysList.chunked(7).forEach { week ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                week.forEach { dayNum ->
                                    val isSelected = dayNum == selectedDayNumber
                                    val hasEvent = dayNum % 3 == 0 || dayNum == 15 || dayNum == 28

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .clickable { selectedDayNumber = dayNum }
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary
                                                else Color.Transparent
                                            )
                                            .size(38.dp),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "$dayNum",
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                        if (hasEvent && !isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primary)
                                            )
                                        }
                                    }
                                }
                                // Fill remaining slots if chunk < 7
                                repeat(7 - week.size) {
                                    Spacer(modifier = Modifier.size(38.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Color Legend Strip
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                LegendDot("Class", Color(0xFF2563EB))
                LegendDot("Homework", Color(0xFFEA580C))
                LegendDot("Exam", Color(0xFFF59E0B))
                LegendDot("Goal", Color(0xFF8B5CF6))
            }
        }

        // Selected Day Activity Schedule
        item {
            Text(
                text = "Activities for Day $selectedDayNumber",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ActivityCard(
                    type = "CLASS",
                    title = "Mathematics Calculus",
                    time = "09:00 - 10:15 AM",
                    location = "Room 302",
                    color = Color(0xFF2563EB)
                )
                ActivityCard(
                    type = "HOMEWORK",
                    title = "Calculus Problem Set 6 Due",
                    time = "11:59 PM",
                    location = "Online Portal",
                    color = Color(0xFFEA580C)
                )
                ActivityCard(
                    type = "SESSION",
                    title = "Pomodoro Study Block (CS AVL Trees)",
                    time = "04:00 PM - 04:50 PM",
                    location = "Self Study",
                    color = Color(0xFF10B981)
                )
                ActivityCard(
                    type = "EXAM",
                    title = "Midterm Calculus Examination",
                    time = "10 Days Countdown Active",
                    location = "Main Hall A",
                    color = Color(0xFFF59E0B)
                )
            }
        }
    }
}

@Composable
private fun LegendDot(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ActivityCard(type: String, title: String, time: String, location: String, color: Color) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = color.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = type,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = color,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("$time • $location", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
