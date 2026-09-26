package com.example.ui.screens.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.model.TimetableItem
import com.example.ui.components.EmptyStateView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(
    timetable: List<TimetableItem>,
    onAddItem: (TimetableItem) -> Unit,
    onUpdateItem: (TimetableItem) -> Unit,
    onDeleteItem: (String) -> Unit
) {
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    var selectedDay by remember { mutableStateOf("Monday") }

    var showAddDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<TimetableItem?>(null) }

    val filteredItems = timetable.filter { it.dayOfWeek.equals(selectedDay, ignoreCase = true) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingItem = null
                    showAddDialog = true
                },
                modifier = Modifier.testTag("timetable_fab_add")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Class")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Day selector tabs
            PrimaryScrollableTabRow(
                selectedTabIndex = days.indexOf(selectedDay),
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                days.forEachIndexed { index, day ->
                    Tab(
                        selected = selectedDay == day,
                        onClick = { selectedDay = day },
                        text = {
                            Text(
                                text = day.take(3),
                                fontWeight = if (selectedDay == day) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.testTag("timetable_tab_${day.lowercase()}")
                    )
                }
            }

            // Summary banner for the day
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$selectedDay's Timetable",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "${filteredItems.size} Classes",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (filteredItems.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.EventBusy,
                    title = "No Classes on $selectedDay",
                    description = "Take this time to relax, revise notes, or work on assignments.",
                    actionButtonLabel = "+ Add Class to $selectedDay",
                    onActionClick = {
                        editingItem = null
                        showAddDialog = true
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredItems, key = { it.id }) { item ->
                        TimetableCard(
                            item = item,
                            onEdit = {
                                editingItem = item
                                showAddDialog = true
                            },
                            onDelete = { onDeleteItem(item.id) }
                        )
                    }
                }
            }
        }

        // Add/Edit Timetable Dialog
        if (showAddDialog) {
            AddEditTimetableDialog(
                initialItem = editingItem,
                defaultDay = selectedDay,
                onDismiss = {
                    showAddDialog = false
                    editingItem = null
                },
                onSave = { item ->
                    if (editingItem != null) {
                        onUpdateItem(item)
                    } else {
                        onAddItem(item)
                    }
                    showAddDialog = false
                    editingItem = null
                }
            )
        }
    }
}

@Composable
private fun TimetableCard(
    item: TimetableItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val barColor = try {
        Color(android.graphics.Color.parseColor(item.colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color indicator
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(barColor)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.subject,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                    ) {
                        Text(
                            text = "${item.startTime} - ${item.endTime}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.teacherName.ifBlank { "Teacher" },
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(
                        imageVector = Icons.Default.Room,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.room.ifBlank { "Room" },
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (item.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Notes: ${item.notes}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }

            // Edit & Delete actions
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Class", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Class", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun AddEditTimetableDialog(
    initialItem: TimetableItem?,
    defaultDay: String,
    onDismiss: () -> Unit,
    onSave: (TimetableItem) -> Unit
) {
    var subject by remember { mutableStateOf(initialItem?.subject ?: "") }
    var teacher by remember { mutableStateOf(initialItem?.teacherName ?: "") }
    var room by remember { mutableStateOf(initialItem?.room ?: "") }
    var day by remember { mutableStateOf(initialItem?.dayOfWeek ?: defaultDay) }
    var startTime by remember { mutableStateOf(initialItem?.startTime ?: "09:00") }
    var endTime by remember { mutableStateOf(initialItem?.endTime ?: "10:00") }
    var notes by remember { mutableStateOf(initialItem?.notes ?: "") }
    var selectedColor by remember { mutableStateOf(initialItem?.colorHex ?: "#2563EB") }

    val colorOptions = listOf("#2563EB", "#7C3AED", "#059669", "#EA580C", "#06B6D4", "#E11D48")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialItem != null) "Edit Class" else "Add New Class") },
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
                    modifier = Modifier.fillMaxWidth().testTag("timetable_dialog_subject")
                )
                OutlinedTextField(
                    value = teacher,
                    onValueChange = { teacher = it },
                    label = { Text("Teacher Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Room / Hall / Lab Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End Time") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Topics") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Color Category:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colorOptions.forEach { hex ->
                        val color = Color(android.graphics.Color.parseColor(hex))
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { selectedColor = hex }
                                .then(
                                    if (selectedColor == hex) Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.4f)) else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedColor == hex) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subject.isNotBlank()) {
                        val item = (initialItem ?: TimetableItem()).copy(
                            subject = subject.trim(),
                            teacherName = teacher.trim(),
                            room = room.trim(),
                            dayOfWeek = day,
                            startTime = startTime.trim(),
                            endTime = endTime.trim(),
                            notes = notes.trim(),
                            colorHex = selectedColor
                        )
                        onSave(item)
                    }
                },
                modifier = Modifier.testTag("timetable_dialog_save")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
