package com.example.ui.screens.exams

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
import com.example.model.ExamItem
import com.example.ui.components.EmptyStateView
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    exams: List<ExamItem>,
    onAddExam: (ExamItem) -> Unit,
    onUpdateExam: (ExamItem) -> Unit,
    onDeleteExam: (String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingExam by remember { mutableStateOf<ExamItem?>(null) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Upcoming, 1: Completed

    val upcomingExams = exams.filter { !it.isCompleted }
    val completedExams = exams.filter { it.isCompleted }
    val currentList = if (selectedTab == 0) upcomingExams else completedExams

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingExam = null
                    showAddDialog = true
                },
                modifier = Modifier.testTag("exams_fab_add")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Exam")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Upcoming (${upcomingExams.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Completed (${completedExams.size})", fontWeight = FontWeight.Bold) }
                )
            }

            if (currentList.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.School,
                    title = if (selectedTab == 0) "No Upcoming Exams" else "No Completed Exams",
                    description = if (selectedTab == 0) "You are all caught up! Prepare for your upcoming semester goals." else "Exams marked completed will appear here.",
                    actionButtonLabel = "+ Schedule Exam",
                    onActionClick = {
                        editingExam = null
                        showAddDialog = true
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(currentList, key = { it.id }) { exam ->
                        ExamCard(
                            exam = exam,
                            onToggleComplete = {
                                onUpdateExam(exam.copy(isCompleted = !exam.isCompleted))
                            },
                            onEdit = {
                                editingExam = exam
                                showAddDialog = true
                            },
                            onDelete = { onDeleteExam(exam.id) }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddEditExamDialog(
                initialExam = editingExam,
                onDismiss = {
                    showAddDialog = false
                    editingExam = null
                },
                onSave = { exam ->
                    if (editingExam != null) {
                        onUpdateExam(exam)
                    } else {
                        onAddExam(exam)
                    }
                    showAddDialog = false
                    editingExam = null
                }
            )
        }
    }
}

@Composable
private fun ExamCard(
    exam: ExamItem,
    onToggleComplete: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val countdownText = remember(exam.examDate) {
        try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val examDate = format.parse(exam.examDate)
            if (examDate != null) {
                val diffMillis = examDate.time - System.currentTimeMillis()
                val diffDays = TimeUnit.MILLISECONDS.toDays(diffMillis).toInt()
                when {
                    diffDays < 0 -> "Passed"
                    diffDays == 0 -> "Today!"
                    diffDays == 1 -> "Tomorrow"
                    else -> "$diffDays Days Left"
                }
            } else {
                "Upcoming"
            }
        } catch (e: Exception) {
            "Upcoming"
        }
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = exam.subject,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (countdownText.contains("Today") || countdownText.contains("Tomorrow")) Color(0xFFFEE2E2) else Color(0xFFFEF3C7)
                ) {
                    Text(
                        text = countdownText,
                        color = if (countdownText.contains("Today") || countdownText.contains("Tomorrow")) Color(0xFFDC2626) else Color(0xFFB45309),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = exam.examName,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(exam.examDate, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(12.dp))
                Icon(Icons.Default.AccessTime, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(exam.examTime, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Room, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Location: ${exam.examLocation}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (exam.syllabus.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Syllabus:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Text(exam.syllabus, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = exam.isCompleted, onCheckedChange = { onToggleComplete() })
                    Text(
                        text = if (exam.isCompleted) "Completed" else "Mark Complete",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditExamDialog(
    initialExam: ExamItem?,
    onDismiss: () -> Unit,
    onSave: (ExamItem) -> Unit
) {
    var examName by remember { mutableStateOf(initialExam?.examName ?: "") }
    var subject by remember { mutableStateOf(initialExam?.subject ?: "Mathematics") }
    var examDate by remember { mutableStateOf(initialExam?.examDate ?: "2026-10-15") }
    var examTime by remember { mutableStateOf(initialExam?.examTime ?: "09:30 AM") }
    var examLocation by remember { mutableStateOf(initialExam?.examLocation ?: "Hall A") }
    var syllabus by remember { mutableStateOf(initialExam?.syllabus ?: "") }
    var notes by remember { mutableStateOf(initialExam?.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialExam != null) "Edit Exam" else "Add Exam") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = examName,
                    onValueChange = { examName = it },
                    label = { Text("Exam Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("exam_dialog_name")
                )
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = examDate,
                        onValueChange = { examDate = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = examTime,
                        onValueChange = { examTime = it },
                        label = { Text("Time") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = examLocation,
                    onValueChange = { examLocation = it },
                    label = { Text("Location / Room") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = syllabus,
                    onValueChange = { syllabus = it },
                    label = { Text("Syllabus & Topics") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (examName.isNotBlank()) {
                        val exam = (initialExam ?: ExamItem()).copy(
                            examName = examName.trim(),
                            subject = subject.trim(),
                            examDate = examDate.trim(),
                            examTime = examTime.trim(),
                            examLocation = examLocation.trim(),
                            syllabus = syllabus.trim(),
                            notes = notes.trim()
                        )
                        onSave(exam)
                    }
                },
                modifier = Modifier.testTag("exam_dialog_save")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
