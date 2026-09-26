package com.example.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.model.*
import com.example.ui.components.EmptyStateView
import com.example.viewmodel.AppDestination

data class SearchResult(
    val id: String,
    val type: String, // NOTE, HOMEWORK, EXAM, GOAL, TIMETABLE
    val title: String,
    val subtitle: String,
    val destination: AppDestination,
    val color: Color
)

@Composable
fun GlobalSearchScreen(
    notes: List<NoteItem>,
    homework: List<HomeworkItem>,
    exams: List<ExamItem>,
    goals: List<GoalItem>,
    timetable: List<TimetableItem>,
    onNavigate: (AppDestination) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filterCategories = listOf("All", "Notes", "Homework", "Exams", "Goals", "Timetable")

    val allResults = remember(query, notes, homework, exams, goals, timetable) {
        val list = mutableListOf<SearchResult>()

        notes.forEach { n ->
            if (query.isBlank() || n.title.contains(query, true) || n.description.contains(query, true) || n.subject.contains(query, true)) {
                list.add(SearchResult(n.id, "Note", n.title, "${n.subject} • ${n.description.take(40)}...", AppDestination.NOTES, Color(0xFF7C3AED)))
            }
        }

        homework.forEach { h ->
            if (query.isBlank() || h.title.contains(query, true) || h.description.contains(query, true) || h.subject.contains(query, true)) {
                list.add(SearchResult(h.id, "Homework", h.title, "${h.subject} • Due ${h.dueDate}", AppDestination.HOMEWORK, Color(0xFFEA580C)))
            }
        }

        exams.forEach { e ->
            if (query.isBlank() || e.examName.contains(query, true) || e.subject.contains(query, true) || e.syllabus.contains(query, true)) {
                list.add(SearchResult(e.id, "Exam", e.examName, "${e.subject} • ${e.examDate} at ${e.examLocation}", AppDestination.EXAMS, Color(0xFFF59E0B)))
            }
        }

        goals.forEach { g ->
            if (query.isBlank() || g.title.contains(query, true) || g.subject.contains(query, true)) {
                list.add(SearchResult(g.id, "Goal", g.title, "${g.subject} • ${g.progressPercentage}% Complete", AppDestination.GOALS, Color(0xFF10B981)))
            }
        }

        timetable.forEach { t ->
            if (query.isBlank() || t.subject.contains(query, true) || t.teacherName.contains(query, true) || t.room.contains(query, true)) {
                list.add(SearchResult(t.id, "Timetable", t.subject, "${t.dayOfWeek} ${t.startTime}-${t.endTime} • ${t.room}", AppDestination.TIMETABLE, Color(0xFF2563EB)))
            }
        }

        list
    }

    val filteredResults = allResults.filter {
        selectedFilter == "All" || it.type.equals(selectedFilter, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("global_search_screen_root")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search notes, tasks, exams, goals...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("global_search_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filterCategories) { cat ->
                    FilterChip(
                        selected = selectedFilter == cat,
                        onClick = { selectedFilter = cat },
                        label = { Text(cat) },
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }

        if (filteredResults.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.SearchOff,
                title = "No Matches Found",
                description = "Try searching for a subject like 'Mathematics' or 'Calculus'."
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredResults, key = { "${it.type}_${it.id}" }) { res ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(res.destination) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = res.color.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = res.type,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = res.color,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(res.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(res.subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }
    }
}
