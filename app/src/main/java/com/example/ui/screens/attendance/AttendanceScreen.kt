package com.example.ui.screens.attendance

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AttendanceItem
import com.example.ui.components.EmptyStateView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    attendanceList: List<AttendanceItem>,
    onAddAttendance: (AttendanceItem) -> Unit,
    onUpdateAttendance: (AttendanceItem) -> Unit,
    onMarkAttendance: (String, Boolean) -> Unit,
    onDeleteAttendance: (String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<AttendanceItem?>(null) }

    val lowAttendanceSubjects = attendanceList.filter { it.totalClasses > 0 && it.percentage < it.minPercentageAlert }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingItem = null
                    showAddDialog = true
                },
                modifier = Modifier.testTag("attendance_fab_add")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Subject")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Low attendance alert banner (if any below threshold)
            if (lowAttendanceSubjects.isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Alert",
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Attendance Alert! ⚠️",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF991B1B)
                            )
                            Text(
                                text = "${lowAttendanceSubjects.joinToString { it.subject }} has fallen below the 75% attendance criteria. Attend your upcoming classes!",
                                fontSize = 12.sp,
                                color = Color(0xFFB91C1C)
                            )
                        }
                    }
                }
            }

            if (attendanceList.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.FactCheck,
                    title = "No Attendance Trackers",
                    description = "Track your attendance across subjects to make sure you stay above minimum academic requirements.",
                    actionButtonLabel = "+ Add Subject",
                    onActionClick = {
                        editingItem = null
                        showAddDialog = true
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(attendanceList, key = { it.id }) { item ->
                        AttendanceCard(
                            item = item,
                            onMarkPresent = { onMarkAttendance(item.id, true) },
                            onMarkAbsent = { onMarkAttendance(item.id, false) },
                            onEdit = {
                                editingItem = item
                                showAddDialog = true
                            },
                            onDelete = { onDeleteAttendance(item.id) }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddEditAttendanceDialog(
                initialItem = editingItem,
                onDismiss = {
                    showAddDialog = false
                    editingItem = null
                },
                onSave = { item ->
                    if (editingItem != null) {
                        onUpdateAttendance(item)
                    } else {
                        onAddAttendance(item)
                    }
                    showAddDialog = false
                    editingItem = null
                }
            )
        }
    }
}

@Composable
private fun AttendanceCard(
    item: AttendanceItem,
    onMarkPresent: () -> Unit,
    onMarkAbsent: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isLow = item.totalClasses > 0 && item.percentage < item.minPercentageAlert
    val statusColor = if (isLow) Color(0xFFEF4444) else Color(0xFF10B981)

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
                Text(
                    text = item.subject,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${item.percentage}%",
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { item.percentage / 100f },
                color = statusColor,
                trackColor = statusColor.copy(alpha = 0.15f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Total: ${item.totalClasses}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Attended: ${item.attendedClasses}",
                        fontSize = 13.sp,
                        color = Color(0xFF059669),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Absent: ${item.absentClasses}",
                        fontSize = 13.sp,
                        color = Color(0xFFDC2626),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Tap Actions: +1 Present, +1 Absent
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onMarkPresent,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    modifier = Modifier.weight(1f).height(38.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("+1 Present", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onMarkAbsent,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    modifier = Modifier.weight(1f).height(38.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("+1 Absent", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AddEditAttendanceDialog(
    initialItem: AttendanceItem?,
    onDismiss: () -> Unit,
    onSave: (AttendanceItem) -> Unit
) {
    var subject by remember { mutableStateOf(initialItem?.subject ?: "") }
    var attended by remember { mutableStateOf(initialItem?.attendedClasses?.toString() ?: "0") }
    var absent by remember { mutableStateOf(initialItem?.absentClasses?.toString() ?: "0") }
    var alertThreshold by remember { mutableStateOf(initialItem?.minPercentageAlert?.toString() ?: "75") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialItem != null) "Edit Attendance" else "Add Subject Attendance") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("attendance_dialog_subject")
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = attended,
                        onValueChange = { attended = it },
                        label = { Text("Attended Classes") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = absent,
                        onValueChange = { absent = it },
                        label = { Text("Absent Classes") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = alertThreshold,
                    onValueChange = { alertThreshold = it },
                    label = { Text("Min % Threshold Alert (e.g. 75)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subject.isNotBlank()) {
                        val attVal = attended.toIntOrNull() ?: 0
                        val absVal = absent.toIntOrNull() ?: 0
                        val threshVal = alertThreshold.toIntOrNull() ?: 75
                        val item = (initialItem ?: AttendanceItem()).copy(
                            subject = subject.trim(),
                            totalClasses = attVal + absVal,
                            attendedClasses = attVal,
                            absentClasses = absVal,
                            minPercentageAlert = threshVal
                        )
                        onSave(item)
                    }
                },
                modifier = Modifier.testTag("attendance_dialog_save")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
