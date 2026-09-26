package com.example.ui.screens.goals

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
import com.example.model.GoalItem
import com.example.model.Priority
import com.example.ui.components.EmptyStateView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    goals: List<GoalItem>,
    onAddGoal: (GoalItem) -> Unit,
    onUpdateGoal: (GoalItem) -> Unit,
    onUpdateProgress: (GoalItem, Int) -> Unit,
    onDeleteGoal: (String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingGoal by remember { mutableStateOf<GoalItem?>(null) }

    val completedGoals = goals.count { it.progressPercentage == 100 }
    val totalGoals = goals.size
    val averageProgress = if (totalGoals > 0) goals.sumOf { it.progressPercentage } / totalGoals else 0

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingGoal = null
                    showAddDialog = true
                },
                modifier = Modifier.testTag("goals_fab_add")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Goal")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Summary progress banner
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Goals Achievement",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "$completedGoals of $totalGoals Completed",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { averageProgress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "$averageProgress% Overall Academic Goals Completed",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            if (goals.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Flag,
                    title = "No Study Goals Set",
                    description = "Create goals like 'Complete Mathematics Chapter 5' to stay motivated and track your study milestones.",
                    actionButtonLabel = "+ Set Goal",
                    onActionClick = {
                        editingGoal = null
                        showAddDialog = true
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(goals, key = { it.id }) { goal ->
                        GoalCard(
                            goal = goal,
                            onProgressClick = { newProgress ->
                                onUpdateProgress(goal, newProgress)
                            },
                            onEdit = {
                                editingGoal = goal
                                showAddDialog = true
                            },
                            onDelete = { onDeleteGoal(goal.id) }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddEditGoalDialog(
                initialGoal = editingGoal,
                onDismiss = {
                    showAddDialog = false
                    editingGoal = null
                },
                onSave = { goal ->
                    if (editingGoal != null) {
                        onUpdateGoal(goal)
                    } else {
                        onAddGoal(goal)
                    }
                    showAddDialog = false
                    editingGoal = null
                }
            )
        }
    }
}

@Composable
private fun GoalCard(
    goal: GoalItem,
    onProgressClick: (Int) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val progressSteps = listOf(0, 25, 50, 75, 100)
    val isComplete = goal.progressPercentage >= 100

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isComplete) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
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
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = goal.subject,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isComplete) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFD1FAE5)
                        ) {
                            Text(
                                text = "ACHIEVED 🎉",
                                color = Color(0xFF065F46),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
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
                text = goal.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Target Date: ${goal.targetDate}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Progress",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${goal.progressPercentage}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isComplete) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { goal.progressPercentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (isComplete) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick progress click buttons (0%, 25%, 50%, 75%, 100%)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                progressSteps.forEach { step ->
                    OutlinedButton(
                        onClick = { onProgressClick(step) },
                        shape = RoundedCornerShape(8.dp),
                        colors = if (goal.progressPercentage == step) {
                            ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        } else ButtonDefaults.outlinedButtonColors(),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier
                            .height(30.dp)
                            .testTag("goal_${goal.id}_step_$step")
                    ) {
                        Text("$step%", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditGoalDialog(
    initialGoal: GoalItem?,
    onDismiss: () -> Unit,
    onSave: (GoalItem) -> Unit
) {
    var title by remember { mutableStateOf(initialGoal?.title ?: "") }
    var subject by remember { mutableStateOf(initialGoal?.subject ?: "Mathematics") }
    var targetDate by remember { mutableStateOf(initialGoal?.targetDate ?: "2026-10-10") }
    var progress by remember { mutableStateOf(initialGoal?.progressPercentage ?: 0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialGoal != null) "Edit Goal" else "Create Study Goal") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Goal Title *") },
                    placeholder = { Text("e.g. Complete Mathematics Chapter 5") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("goal_dialog_title")
                )
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = targetDate,
                    onValueChange = { targetDate = it },
                    label = { Text("Target Date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Current Progress: $progress%", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Slider(
                    value = progress.toFloat(),
                    onValueChange = { progress = it.toInt() },
                    valueRange = 0f..100f,
                    steps = 3 // 25, 50, 75
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val goal = (initialGoal ?: GoalItem()).copy(
                            title = title.trim(),
                            subject = subject.trim(),
                            targetDate = targetDate.trim(),
                            progressPercentage = progress
                        )
                        onSave(goal)
                    }
                },
                modifier = Modifier.testTag("goal_dialog_save")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
