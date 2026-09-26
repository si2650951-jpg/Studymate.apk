package com.example.ui.screens.timer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.model.StudySession
import com.example.viewmodel.TimerUiState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyTimerScreen(
    timerState: TimerUiState,
    sessions: List<StudySession>,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit,
    onReset: () -> Unit,
    onSetMode: (Boolean) -> Unit,
    onSetSubject: (String) -> Unit
) {
    val minutes = timerState.remainingSeconds / 60
    val seconds = timerState.remainingSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)
    val progress = if (timerState.totalSeconds > 0) {
        timerState.remainingSeconds.toFloat() / timerState.totalSeconds.toFloat()
    } else 0f

    val subjects = listOf("Mathematics", "Computer Science", "Physics", "English Literature", "Chemistry")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("timer_scroll_column"),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mode Switcher (Pomodoro 25/5 vs Deep Focus 45)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = timerState.isPomodoro,
                        onClick = { onSetMode(true) },
                        label = { Text("Pomodoro (25m / 5m)") },
                        leadingIcon = { Icon(Icons.Default.HourglassBottom, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        shape = RoundedCornerShape(12.dp)
                    )
                    FilterChip(
                        selected = !timerState.isPomodoro,
                        onClick = { onSetMode(false) },
                        label = { Text("Deep Focus (45m)") },
                        leadingIcon = { Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Subject Selector
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Subject: ", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    var expanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                        TextButton(onClick = { expanded = true }) {
                            Text(timerState.selectedSubject, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            subjects.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s) },
                                    onClick = {
                                        onSetSubject(s)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Big Circular Timer Visual
        item {
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(
                        if (timerState.isBreak) Color(0xFFD1FAE5)
                        else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(230.dp),
                    strokeWidth = 10.dp,
                    color = if (timerState.isBreak) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (timerState.isBreak) "BREAK TIME ☕" else "STUDY SESSION",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (timerState.isBreak) Color(0xFF047857) else MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = formattedTime,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = when {
                            timerState.isPaused -> "PAUSED"
                            timerState.isRunning -> "FOCUSING..."
                            else -> "READY"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Timer Control Buttons
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!timerState.isRunning) {
                    Button(
                        onClick = onStart,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("timer_start_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start Session", fontWeight = FontWeight.Bold)
                    }
                } else if (timerState.isPaused) {
                    Button(
                        onClick = onResume,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("timer_resume_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Resume", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onStop,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("timer_stop_button")
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Finish & Save")
                    }
                } else {
                    Button(
                        onClick = onPause,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("timer_pause_button")
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pause", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onStop,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("timer_stop_button")
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Finish & Save")
                    }
                }

                IconButton(
                    onClick = onReset,
                    modifier = Modifier.testTag("timer_reset_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset Timer")
                }
            }
        }

        // Recent Sessions Log
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Study Sessions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${sessions.size} logged",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(sessions.take(6), key = { it.id }) { session ->
            val dateStr = remember(session.date) {
                SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(session.date))
            }
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(session.subject, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            text = session.notes.ifBlank { "Focus session" },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(dateStr, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "${session.durationMinutes} mins",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}
