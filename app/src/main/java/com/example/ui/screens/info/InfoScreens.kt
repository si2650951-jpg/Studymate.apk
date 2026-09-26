package com.example.ui.screens.info

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.model.SupportTicket
import com.example.ui.theme.StudyMateBlue
import com.example.ui.theme.StudyMateGradient

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(StudyMateGradient),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(44.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "About StudyMate",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Version 1.0.0 • Academic Edition",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "StudyMate helps students organize their studies, manage homework, track exams, create notes, build study goals and monitor their academic progress from one simple application.",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )

                Text(
                    text = "Key Highlights:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                AboutBullet("📅 Full 7-Day Timetable with teacher and room tracking")
                AboutBullet("📝 Rich digital notes with subject tags and pinning")
                AboutBullet("⏱️ Built-in Pomodoro Study Timer (25m study / 5m break)")
                AboutBullet("📊 Live attendance tracking with minimum percentage warnings")
                AboutBullet("🎯 Goal setting with progress milestones & celebration animations")
                AboutBullet("🔒 Secure cloud storage with role-based student data isolation")
            }
        }
    }
}

@Composable
private fun AboutBullet(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun PrivacyPolicyScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Privacy Policy",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Last updated: September 2026. Your privacy and academic integrity are essential to us.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        PolicyCard("1. Account Information", "When registering an account on StudyMate, we collect your name, email address, mobile number, educational institution, and course. This information is solely used to personalize your study management experience.")
        PolicyCard("2. Student Data & Notes Isolation", "All notes, homework assignments, timetable items, and study goals created by you are strictly private. Firebase Security Rules ensure only your authenticated account can read, edit, or delete your study data. Administrators cannot access your private student notes.")
        PolicyCard("3. Firebase Storage & Cloud Sync", "Your notes and study sessions are synchronized with Google Firebase Firestore and Firebase Storage using industry-standard TLS encryption. You can export or clear your local cache at any time.")
        PolicyCard("4. Notifications & Alerts", "Study reminders and attendance threshold alerts are scheduled to notify you of upcoming deadlines. You can customize or disable individual notification categories in Settings.")
        PolicyCard("5. Data Deletion", "You retain the right to delete your study materials or close your account. Contact support or use account deletion under Profile settings to permanently wipe all associated data.")
    }
}

@Composable
fun TermsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Terms & Conditions",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Please read these terms carefully before using StudyMate.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        PolicyCard("1. User Responsibilities", "Users are responsible for maintaining the confidentiality of their credentials and ensuring all academic materials uploaded comply with their school or institution's honor code.")
        PolicyCard("2. Account Usage", "Each student account is intended for individual educational use. Impersonation of educators or academic administration is strictly prohibited.")
        PolicyCard("3. Content Ownership", "You retain full intellectual property ownership of all student notes, study summaries, and materials you enter into StudyMate.")
        PolicyCard("4. Service Availability", "StudyMate includes offline support to ensure your study schedules and notes remain accessible during connectivity outages.")
    }
}

@Composable
private fun PolicyCard(title: String, content: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(6.dp))
            Text(content, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
        }
    }
}

@Composable
fun HelpSupportScreen(
    onSubmitTicket: (SupportTicket) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var submittedMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Help & Support",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // FAQ Section
        Text(
            text = "Frequently Asked Questions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        FaqAccordionItem(
            question = "How does the Pomodoro timer work?",
            answer = "The Pomodoro timer runs for 25 minutes of deep focus followed by an automatic 5-minute break. Completed sessions are logged to your Progress statistics."
        )

        FaqAccordionItem(
            question = "How is attendance percentage calculated?",
            answer = "Attendance is calculated as (Attended Classes / Total Classes) * 100. If your attendance falls below your chosen threshold (default 75%), a reminder banner appears."
        )

        FaqAccordionItem(
            question = "Can I switch app themes?",
            answer = "Yes! StudyMate includes 6 themes: Royal Blue, Vibrant Purple, Emerald Green, Sunset Orange, Dark Navy, and System Dark Mode. You can change them anytime in Settings."
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Contact Support Form
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Contact Support / Report a Problem",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                if (submittedMessage != null) {
                    Surface(
                        color = Color(0xFFD1FAE5),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = submittedMessage ?: "",
                            color = Color(0xFF065F46),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Your Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Your Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Describe your issue or feedback") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (name.isNotBlank() && email.isNotBlank() && message.isNotBlank()) {
                            onSubmitTicket(
                                SupportTicket(
                                    name = name.trim(),
                                    email = email.trim(),
                                    subject = subject.trim().ifBlank { "Support Inquiry" },
                                    message = message.trim()
                                )
                            )
                            submittedMessage = "Thank you! Your ticket has been submitted to StudyMate Support."
                            name = ""
                            email = ""
                            subject = ""
                            message = ""
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("support_submit_button")
                ) {
                    Text("Submit Support Request")
                }
            }
        }
    }
}

@Composable
private fun FaqAccordionItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(question, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(answer, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 19.sp)
                }
            }
        }
    }
}
