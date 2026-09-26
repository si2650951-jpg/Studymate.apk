package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickActionBottomSheet(
    onDismiss: () -> Unit,
    onAddTask: () -> Unit,
    onAddNote: () -> Unit,
    onAddTimetable: () -> Unit,
    onAddExam: () -> Unit,
    onStartTimer: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Quick Actions",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "What would you like to create or do today?",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                QuickActionButton(
                    icon = Icons.Default.Assignment,
                    label = "Add Task",
                    bg = Color(0xFF3B82F6),
                    onClick = {
                        onDismiss()
                        onAddTask()
                    }
                )
                QuickActionButton(
                    icon = Icons.Default.MenuBook,
                    label = "Add Note",
                    bg = Color(0xFF8B5CF6),
                    onClick = {
                        onDismiss()
                        onAddNote()
                    }
                )
                QuickActionButton(
                    icon = Icons.Default.CalendarToday,
                    label = "Timetable",
                    bg = Color(0xFF10B981),
                    onClick = {
                        onDismiss()
                        onAddTimetable()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                QuickActionButton(
                    icon = Icons.Default.School,
                    label = "Add Exam",
                    bg = Color(0xFFF59E0B),
                    onClick = {
                        onDismiss()
                        onAddExam()
                    }
                )
                QuickActionButton(
                    icon = Icons.Default.Timer,
                    label = "Study Timer",
                    bg = Color(0xFFEC4899),
                    onClick = {
                        onDismiss()
                        onStartTimer()
                    }
                )
                Box(modifier = Modifier.size(72.dp)) // Spacer for alignment
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    bg: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
            .testTag("quick_action_${label.lowercase().replace(" ", "_")}")
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(bg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
