package com.example.ui.screens.homework

import androidx.compose.foundation.clickable
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
import com.example.model.HomeworkItem
import com.example.model.HomeworkStatus
import com.example.model.Priority
import com.example.ui.components.EmptyStateView
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeworkScreen(
    homeworkList: List<HomeworkItem>,
    onAddHomework: (HomeworkItem) -> Unit,
    onUpdateHomework: (HomeworkItem) -> Unit,
    onUpdateStatus: (String, HomeworkStatus) -> Unit,
    onDeleteHomework: (String) -> Unit
) {
    val tabs = listOf("All", "Pending", "In Progress", "Completed")
    var selectedTab by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingHomework by remember { mutableStateOf<HomeworkItem?>(null) }

    val filteredList = homeworkList.filter { item ->
        when (selectedTab) {
            "Pending" -> item.status == HomeworkStatus.PENDING
            "In Progress" -> item.status == HomeworkStatus.IN_PROGRESS
            "Completed" -> item.status == HomeworkStatus.COMPLETED
            else -> true
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingHomework = null
                    showAddDialog = true
                },
                modifier = Modifier.testTag("homework_fab_add")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Homework")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            PrimaryTabRow(
                selectedTabIndex = tabs.indexOf(selectedTab),
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = { Text(tab, fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal) },
                        modifier = Modifier.testTag("homework_tab_${tab.lowercase().replace(" ", "_")}")
                    )
                }
            }

            if (filteredList.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.AssignmentTurnedIn,
                    title = if (selectedTab == "Completed") "No Completed Homework" else "No Homework Yet",
                    description = if (selectedTab == "Completed") "Finish a task to see it here!" else "Add your first homework task to keep track of deadlines.",
                    actionButtonLabel = "+ Add Homework",
                    onActionClick = {
                        editingHomework = null
                        showAddDialog = true
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        HomeworkCard(
                            item = item,
                            onStatusChange = { newStatus -> onUpdateStatus(item.id, newStatus) },
                            onEdit = {
                                editingHomework = item
                                showAddDialog = true
                            },
                            onDelete = { onDeleteHomework(item.id) }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddEditHomeworkDialog(
                initialItem = editingHomework,
                onDismiss = {
                    showAddDialog = false
                    editingHomework = null
                },
                onSave = { item ->
                    if (editingHomework != null) {
                        onUpdateHomework(item)
                    } else {
                        onAddHomework(item)
                    }
                    showAddDialog = false
                    editingHomework = null
                }
            )
        }
    }
}

@Composable
private fun HomeworkCard(
    item: HomeworkItem,
    onStatusChange: (HomeworkStatus) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val priorityColor = when (item.priority) {
        Priority.HIGH -> Color(0xFFEF4444)
        Priority.MEDIUM -> Color(0xFFF59E0B)
        Priority.LOW -> Color(0xFF10B981)
    }

    val isDone = item.status == HomeworkStatus.COMPLETED

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = item.subject,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = priorityColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${item.priority} Priority",
                            color = priorityColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
            )

            if (item.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Due: ${item.dueDate}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Status dropdown or toggle chip
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    StatusChip("Pending", item.status == HomeworkStatus.PENDING) {
                        onStatusChange(HomeworkStatus.PENDING)
                    }
                    StatusChip("In Progress", item.status == HomeworkStatus.IN_PROGRESS) {
                        onStatusChange(HomeworkStatus.IN_PROGRESS)
                    }
                    StatusChip("Done", item.status == HomeworkStatus.COMPLETED) {
                        onStatusChange(HomeworkStatus.COMPLETED)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 11.sp) },
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
fun AddEditHomeworkDialog(
    initialItem: HomeworkItem?,
    onDismiss: () -> Unit,
    onSave: (HomeworkItem) -> Unit
) {
    var subject by remember { mutableStateOf(initialItem?.subject ?: "Mathematics") }
    var title by remember { mutableStateOf(initialItem?.title ?: "") }
    var description by remember { mutableStateOf(initialItem?.description ?: "") }
    var dueDate by remember { mutableStateOf(initialItem?.dueDate ?: "2026-10-02") }
    var priority by remember { mutableStateOf(initialItem?.priority ?: Priority.MEDIUM) }
    var status by remember { mutableStateOf(initialItem?.status ?: HomeworkStatus.PENDING) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialItem != null) "Edit Homework" else "Add Homework") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Homework Title *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("homework_dialog_title")
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Instructions") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Priority Level:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Priority.values().forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p.name) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val item = (initialItem ?: HomeworkItem()).copy(
                            subject = subject.trim(),
                            title = title.trim(),
                            description = description.trim(),
                            dueDate = dueDate.trim(),
                            priority = priority,
                            status = status
                        )
                        onSave(item)
                    }
                },
                modifier = Modifier.testTag("homework_dialog_save")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
