package com.example.ui.screens.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NotificationItem
import com.example.model.NotificationPreferences
import com.example.ui.components.EmptyStateView
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationsScreen(
    notifications: List<NotificationItem>,
    preferences: NotificationPreferences,
    onDismissNotification: (String) -> Unit,
    onClearAll: () -> Unit,
    onUpdatePreferences: (NotificationPreferences) -> Unit
) {
    var showFilterSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("notifications_screen_root")
    ) {
        // Preferences Quick Controls Strip
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Notification Alerts",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Customize reminder triggers",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row {
                    TextButton(onClick = { showFilterSheet = true }) {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Configure")
                    }
                    if (notifications.isNotEmpty()) {
                        TextButton(onClick = onClearAll) {
                            Text("Clear All", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        if (notifications.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.NotificationsNone,
                title = "No New Notifications",
                description = "You're all caught up! Upcoming classes, exams, homework deadlines, and attendance alerts will appear here."
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications, key = { it.id }) { item ->
                    NotificationCard(
                        item = item,
                        onDismiss = { onDismissNotification(item.id) }
                    )
                }
            }
        }

        if (showFilterSheet) {
            AlertDialog(
                onDismissRequest = { showFilterSheet = false },
                title = { Text("Notification Categories") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        NotificationToggleRow("Upcoming Classes", preferences.upcomingClass) {
                            onUpdatePreferences(preferences.copy(upcomingClass = it))
                        }
                        NotificationToggleRow("Homework Deadlines", preferences.homeworkDeadline) {
                            onUpdatePreferences(preferences.copy(homeworkDeadline = it))
                        }
                        NotificationToggleRow("Exam Reminders", preferences.examReminder) {
                            onUpdatePreferences(preferences.copy(examReminder = it))
                        }
                        NotificationToggleRow("Study Goals Progress", preferences.studyGoal) {
                            onUpdatePreferences(preferences.copy(studyGoal = it))
                        }
                        NotificationToggleRow("Study Session Reminders", preferences.studySession) {
                            onUpdatePreferences(preferences.copy(studySession = it))
                        }
                        NotificationToggleRow("Attendance Warnings", preferences.attendanceWarning) {
                            onUpdatePreferences(preferences.copy(attendanceWarning = it))
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { showFilterSheet = false }) { Text("Done") }
                }
            )
        }
    }
}

@Composable
private fun NotificationToggleRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 13.sp)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun NotificationCard(item: NotificationItem, onDismiss: () -> Unit) {
    val icon = when (item.type) {
        "CLASS" -> Icons.Default.CalendarToday
        "HOMEWORK" -> Icons.Default.Assignment
        "EXAM" -> Icons.Default.School
        "ATTENDANCE" -> Icons.Default.Warning
        else -> Icons.Default.Notifications
    }

    val iconColor = when (item.type) {
        "CLASS" -> Color(0xFF2563EB)
        "HOMEWORK" -> Color(0xFFEA580C)
        "EXAM" -> Color(0xFFF59E0B)
        "ATTENDANCE" -> Color(0xFFDC2626)
        else -> MaterialTheme.colorScheme.primary
    }

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
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = iconColor.copy(alpha = 0.15f)
            ) {
                Box(modifier = Modifier.padding(10.dp)) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(item.message, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
            }
        }
    }
}
