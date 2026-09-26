package com.example.ui.screens.notes

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.model.CourseItem
import com.example.model.NoteItem
import com.example.ui.components.EmptyStateView
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    notes: List<NoteItem>,
    courses: List<CourseItem> = emptyList(),
    onAddCourse: ((CourseItem) -> Unit)? = null,
    onAddNote: (NoteItem) -> Unit,
    onUpdateNote: (NoteItem) -> Unit,
    onTogglePin: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onDeleteNote: (String) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedSubjectFilter by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }
    var showCourseManagerDialog by remember { mutableStateOf(false) }
    var editingNote by remember { mutableStateOf<NoteItem?>(null) }
    var viewingImageUrl by remember { mutableStateOf<String?>(null) }

    val courseNames = remember(courses, notes) {
        val fromCourses = courses.map { it.name }
        val fromNotes = notes.map { it.subject }
        (listOf("All") + fromCourses + fromNotes).filter { it.isNotBlank() }.distinct()
    }

    val filteredNotes = notes.filter { note ->
        val matchesSubject = selectedSubjectFilter == "All" || note.subject.equals(selectedSubjectFilter, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                note.title.contains(searchQuery, ignoreCase = true) ||
                note.description.contains(searchQuery, ignoreCase = true) ||
                note.subject.contains(searchQuery, ignoreCase = true) ||
                note.tags.any { it.contains(searchQuery, ignoreCase = true) }
        matchesSubject && matchesSearch
    }.sortedWith(compareByDescending<NoteItem> { it.isPinned }.thenByDescending { it.updatedAt })

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingNote = null
                    showAddDialog = true
                },
                modifier = Modifier.testTag("notes_fab_add")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar & Filter Strip
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search title, tags, content...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("notes_search_field")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Course / Subject Filter Row + Manage Courses action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(courseNames) { subject ->
                            FilterChip(
                                selected = selectedSubjectFilter == subject,
                                onClick = { selectedSubjectFilter = subject },
                                label = { Text(subject) },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { showCourseManagerDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = "Courses",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            if (filteredNotes.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.MenuBook,
                    title = "No Notes Found",
                    description = if (searchQuery.isNotBlank()) "No notes match '$searchQuery'" else "Create your first study note with photos, diagrams, and revision summaries.",
                    actionButtonLabel = "+ Create Note",
                    onActionClick = {
                        editingNote = null
                        showAddDialog = true
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredNotes, key = { it.id }) { note ->
                        NoteCard(
                            note = note,
                            onEdit = {
                                editingNote = note
                                showAddDialog = true
                            },
                            onTogglePin = { onTogglePin(note.id) },
                            onToggleFavorite = { onToggleFavorite(note.id) },
                            onDelete = { onDeleteNote(note.id) },
                            onShare = { shareNote(context, note) },
                            onImageClick = { url -> viewingImageUrl = url }
                        )
                    }
                }
            }
        }

        // Add / Edit Note Dialog with Photo Upload & Course Selection
        if (showAddDialog) {
            AddEditNoteDialog(
                initialNote = editingNote,
                courses = courses,
                onDismiss = {
                    showAddDialog = false
                    editingNote = null
                },
                onSave = { note ->
                    if (editingNote != null) {
                        onUpdateNote(note)
                    } else {
                        onAddNote(note)
                    }
                    showAddDialog = false
                    editingNote = null
                }
            )
        }

        // Course Management Dialog
        if (showCourseManagerDialog) {
            CourseManagerDialog(
                courses = courses,
                onAddCourse = { newCourse ->
                    onAddCourse?.invoke(newCourse)
                },
                onDismiss = { showCourseManagerDialog = false }
            )
        }

        // Full Screen Image Viewer Dialog
        if (viewingImageUrl != null) {
            Dialog(onDismissRequest = { viewingImageUrl = null }) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Attached Image / Diagram",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            IconButton(onClick = { viewingImageUrl = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        AsyncImage(
                            model = viewingImageUrl,
                            contentDescription = "Full size note image",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 200.dp, max = 450.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NoteCard(
    note: NoteItem,
    onEdit: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    onImageClick: (String) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val formattedDate = remember(note.updatedAt) { dateFormat.format(Date(note.updatedAt)) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (note.isPinned) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.30f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Subject tag & Action Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = note.subject,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Share Note button
                    IconButton(onClick = onShare, modifier = Modifier.size(32.dp).testTag("note_share_button")) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Note",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onTogglePin, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pin Note",
                            tint = if (note.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onToggleFavorite, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (note.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite Note",
                            tint = if (note.isFavorite) Color(0xFFEF4444) else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = note.title,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = note.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 5
            )

            // Attached Image Preview
            if (note.attachmentUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onImageClick(note.attachmentUrl) }
                ) {
                    AsyncImage(
                        model = note.attachmentUrl,
                        contentDescription = "Note attached diagram",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Surface(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(topStart = 8.dp),
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.ZoomIn, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tap to view", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }

            if (note.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    note.tags.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "#$tag",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Updated: $formattedDate",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )
                if (note.attachmentUrl.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            "1 Image Attached",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteDialog(
    initialNote: NoteItem?,
    courses: List<CourseItem>,
    onDismiss: () -> Unit,
    onSave: (NoteItem) -> Unit
) {
    var title by remember { mutableStateOf(initialNote?.title ?: "") }
    var subject by remember { mutableStateOf(initialNote?.subject ?: (courses.firstOrNull()?.name ?: "Mathematics")) }
    var description by remember { mutableStateOf(initialNote?.description ?: "") }
    var tagsInput by remember { mutableStateOf(initialNote?.tags?.joinToString(", ") ?: "") }
    var attachmentUrl by remember { mutableStateOf(initialNote?.attachmentUrl ?: "") }
    var isPinned by remember { mutableStateOf(initialNote?.isPinned ?: false) }
    var isFavorite by remember { mutableStateOf(initialNote?.isFavorite ?: false) }
    var courseDropdownExpanded by remember { mutableStateOf(false) }

    // Android Zero-permission Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                attachmentUrl = uri.toString()
            }
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialNote != null) "Edit Note" else "Create Study Note") },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Note Title *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("note_dialog_title")
                    )
                }

                // Course / Subject selector with Dropdown
                item {
                    ExposedDropdownMenuBox(
                        expanded = courseDropdownExpanded,
                        onExpandedChange = { courseDropdownExpanded = !courseDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = subject,
                            onValueChange = { subject = it },
                            label = { Text("Course / Subject *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = courseDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = courseDropdownExpanded,
                            onDismissRequest = { courseDropdownExpanded = false }
                        ) {
                            val availableCourses = courses.map { it.name }.ifEmpty {
                                listOf("Mathematics", "Computer Science", "Physics", "Chemistry", "English Literature", "Biology")
                            }.distinct()

                            availableCourses.forEach { courseName ->
                                DropdownMenuItem(
                                    text = { Text(courseName) },
                                    onClick = {
                                        subject = courseName
                                        courseDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Content / Formulas / Summary *") },
                        minLines = 3,
                        maxLines = 6,
                        modifier = Modifier.fillMaxWidth().testTag("note_dialog_desc")
                    )
                }

                item {
                    OutlinedTextField(
                        value = tagsInput,
                        onValueChange = { tagsInput = it },
                        label = { Text("Tags (comma separated)") },
                        placeholder = { Text("e.g. Revision, Formulas, Exam") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Image Upload & Attachment Section
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "Image Attachment",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            if (attachmentUrl.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                ) {
                                    AsyncImage(
                                        model = attachmentUrl,
                                        contentDescription = "Selected photo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    IconButton(
                                        onClick = { attachmentUrl = "" },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .size(28.dp)
                                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Remove photo",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (attachmentUrl.isBlank()) "Choose Photo" else "Change Photo", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isPinned, onCheckedChange = { isPinned = it })
                            Text("Pin Note", fontSize = 13.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isFavorite, onCheckedChange = { isFavorite = it })
                            Text("Favorite", fontSize = 13.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val tagsList = tagsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                        val note = (initialNote ?: NoteItem()).copy(
                            title = title.trim(),
                            subject = subject.trim(),
                            description = description.trim(),
                            tags = tagsList,
                            attachmentUrl = attachmentUrl.trim(),
                            isPinned = isPinned,
                            isFavorite = isFavorite,
                            updatedAt = System.currentTimeMillis()
                        )
                        onSave(note)
                    }
                },
                modifier = Modifier.testTag("note_dialog_save")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun CourseManagerDialog(
    courses: List<CourseItem>,
    onAddCourse: (CourseItem) -> Unit,
    onDismiss: () -> Unit
) {
    var showAddCourseForm by remember { mutableStateOf(false) }
    var courseName by remember { mutableStateOf("") }
    var courseCode by remember { mutableStateOf("") }
    var instructor by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Course & Subject Manager")
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!showAddCourseForm) {
                    Text(
                        "Manage your enrolled courses to organize study notes, timetable, and assignments:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    LazyColumn(
                        modifier = Modifier.weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(courses) { course ->
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(
                                                runCatching { Color(android.graphics.Color.parseColor(course.colorHex)) }.getOrDefault(MaterialTheme.colorScheme.primary),
                                                CircleShape
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(course.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(
                                            "${course.code} • ${course.instructor}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { showAddCourseForm = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add New Course")
                    }
                } else {
                    Text("Add New Course", fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = courseName,
                        onValueChange = { courseName = it },
                        label = { Text("Course Name * (e.g. Data Science)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = courseCode,
                        onValueChange = { courseCode = it },
                        label = { Text("Course Code (e.g. DS 301)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = instructor,
                        onValueChange = { instructor = it },
                        label = { Text("Instructor / Teacher Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Course Description") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showAddCourseForm = false }) {
                            Text("Back")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (courseName.isNotBlank()) {
                                    val newCourse = CourseItem(
                                        name = courseName.trim(),
                                        code = courseCode.trim().ifBlank { "CRS" },
                                        instructor = instructor.trim().ifBlank { "Instructor" },
                                        description = description.trim()
                                    )
                                    onAddCourse(newCourse)
                                    courseName = ""
                                    courseCode = ""
                                    instructor = ""
                                    description = ""
                                    showAddCourseForm = false
                                }
                            }
                        ) {
                            Text("Save Course")
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!showAddCourseForm) {
                TextButton(onClick = onDismiss) { Text("Close") }
            }
        }
    )
}

/**
 * Standard Android Share Intent helper for Notes (text + image).
 */
private fun shareNote(context: Context, note: NoteItem) {
    try {
        val shareText = buildString {
            appendLine("📚 StudyMate – Student Study Note")
            appendLine("────────────────────────────────────")
            appendLine("📖 Title: ${note.title}")
            appendLine("🎓 Course / Subject: ${note.subject}")
            if (note.tags.isNotEmpty()) {
                appendLine("🏷️ Tags: ${note.tags.joinToString(" ") { "#$it" }}")
            }
            appendLine()
            appendLine("📝 Notes & Summary:")
            appendLine(note.description)
            appendLine()
            appendLine("📅 Updated: ${SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(note.updatedAt))}")
            appendLine("────────────────────────────────────")
            appendLine("✨ Shared via StudyMate – Student Study Manager")
        }

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            if (note.attachmentUrl.isNotBlank() && note.attachmentUrl.startsWith("content://")) {
                type = "image/*"
                val imageUri = Uri.parse(note.attachmentUrl)
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_TEXT, shareText)
                putExtra(Intent.EXTRA_SUBJECT, note.title)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
                putExtra(Intent.EXTRA_SUBJECT, note.title)
            }
        }
        val chooser = Intent.createChooser(sendIntent, "Share Study Note via...")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    } catch (_: Exception) {
        val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "${note.title}\n\n${note.description}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(fallbackIntent, "Share Note"))
    }
}
