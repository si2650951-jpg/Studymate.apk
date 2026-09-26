package com.example.ui.screens.admin

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
import com.example.model.*
import com.example.ui.components.EmptyStateView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    students: List<User>,
    announcements: List<Announcement>,
    totalNotes: Int,
    totalHomework: Int,
    totalExams: Int,
    totalStudySessions: Int,
    onAddAnnouncement: (Announcement) -> Unit,
    onDeleteAnnouncement: (String) -> Unit,
    onRemoveStudent: (String) -> Unit
) {
    var selectedSection by remember { mutableStateOf(0) } // 0: Metrics & Reports, 1: Student Management, 2: Announcements
    var studentSearchQuery by remember { mutableStateOf("") }
    var showAddAnnouncementDialog by remember { mutableStateOf(false) }

    val filteredStudents = students.filter {
        studentSearchQuery.isBlank() ||
                it.name.contains(studentSearchQuery, true) ||
                it.email.contains(studentSearchQuery, true) ||
                it.className.contains(studentSearchQuery, true)
    }

    Scaffold(
        floatingActionButton = {
            if (selectedSection == 2) {
                FloatingActionButton(
                    onClick = { showAddAnnouncementDialog = true },
                    modifier = Modifier.testTag("admin_fab_add_announcement")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Announcement")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Privacy Protection Banner
            Surface(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Privacy Protected: Administrators cannot view private student notes.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            PrimaryTabRow(selectedTabIndex = selectedSection) {
                Tab(
                    selected = selectedSection == 0,
                    onClick = { selectedSection = 0 },
                    text = { Text("Metrics", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedSection == 1,
                    onClick = { selectedSection = 1 },
                    text = { Text("Students (${students.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedSection == 2,
                    onClick = { selectedSection = 2 },
                    text = { Text("Announcements", fontWeight = FontWeight.Bold) }
                )
            }

            when (selectedSection) {
                0 -> {
                    // System Overview Metrics
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text(
                                text = "StudyMate Academic Metrics",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    AdminMetricCard("Total Students", "${students.size}", Icons.Default.People, Color(0xFF2563EB), Modifier.weight(1f))
                                    AdminMetricCard("Active Users", "${students.count { it.role == UserRole.STUDENT }}", Icons.Default.CheckCircle, Color(0xFF10B981), Modifier.weight(1f))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    AdminMetricCard("Total Notes", "$totalNotes", Icons.Default.MenuBook, Color(0xFF7C3AED), Modifier.weight(1f))
                                    AdminMetricCard("Homework Tasks", "$totalHomework", Icons.Default.Assignment, Color(0xFFEA580C), Modifier.weight(1f))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    AdminMetricCard("Exams Tracked", "$totalExams", Icons.Default.School, Color(0xFFF59E0B), Modifier.weight(1f))
                                    AdminMetricCard("Study Sessions", "$totalStudySessions", Icons.Default.Timer, Color(0xFF06B6D4), Modifier.weight(1f))
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Server & Database Status",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    SystemStatusRow("Firebase Firestore", "Online • Sync Active", Color(0xFF10B981))
                                    SystemStatusRow("Authentication Engine", "Active (Email/Google)", Color(0xFF10B981))
                                    SystemStatusRow("Security Rules", "Role-based Active", Color(0xFF10B981))
                                    SystemStatusRow("Offline Cache", "Enabled", Color(0xFF2563EB))
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Students List
                    Column(modifier = Modifier.fillMaxSize()) {
                        OutlinedTextField(
                            value = studentSearchQuery,
                            onValueChange = { studentSearchQuery = it },
                            placeholder = { Text("Search by student name or email...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredStudents, key = { it.id }) { std ->
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
                                                .size(44.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = std.name.take(1).uppercase(),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(std.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Text(std.email, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("${std.className} • ${std.schoolName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                        }

                                        IconButton(onClick = { onRemoveStudent(std.id) }) {
                                            Icon(Icons.Default.PersonRemove, contentDescription = "Disable Account", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Announcements Manager
                    if (announcements.isEmpty()) {
                        EmptyStateView(
                            icon = Icons.Default.Campaign,
                            title = "No Announcements",
                            description = "Post announcements to notify students about exams, schedules, and important dates.",
                            actionButtonLabel = "+ Post Announcement",
                            onActionClick = { showAddAnnouncementDialog = true }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(announcements, key = { it.id }) { ann ->
                                Card(
                                    shape = RoundedCornerShape(16.dp),
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
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (ann.isPinned) {
                                                    Icon(Icons.Default.PushPin, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                }
                                                Text(ann.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            }
                                            IconButton(onClick = { onDeleteAnnouncement(ann.id) }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(ann.content, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("By ${ann.authorName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddAnnouncementDialog) {
            var title by remember { mutableStateOf("") }
            var content by remember { mutableStateOf("") }
            var isPinned by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { showAddAnnouncementDialog = false },
                title = { Text("Create Announcement") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Announcement Title *") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = content,
                            onValueChange = { content = it },
                            label = { Text("Content / Announcement Message *") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isPinned, onCheckedChange = { isPinned = it })
                            Text("Pin to student dashboard", fontSize = 13.sp)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                onAddAnnouncement(
                                    Announcement(
                                        title = title.trim(),
                                        content = content.trim(),
                                        authorName = "StudyMate Admin",
                                        isPinned = isPinned
                                    )
                                )
                                showAddAnnouncementDialog = false
                            }
                        }
                    ) {
                        Text("Publish")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddAnnouncementDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
private fun AdminMetricCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun SystemStatusRow(name: String, status: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(status, fontSize = 12.sp, color = color, fontWeight = FontWeight.SemiBold)
        }
    }
}
