package com.example.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.AppThemeMode
import com.example.model.NotificationPreferences
import com.example.viewmodel.AppDestination

@Composable
fun SettingsScreen(
    currentTheme: AppThemeMode,
    currentLanguage: AppLanguage,
    notificationPrefs: NotificationPreferences,
    onSelectTheme: (AppThemeMode) -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit,
    onUpdateNotifications: (NotificationPreferences) -> Unit,
    onNavigate: (AppDestination) -> Unit,
    onLogout: () -> Unit
) {
    val themes = listOf(
        Pair(AppThemeMode.BLUE, Color(0xFF2563EB)),
        Pair(AppThemeMode.PURPLE, Color(0xFF7C3AED)),
        Pair(AppThemeMode.GREEN, Color(0xFF059669)),
        Pair(AppThemeMode.ORANGE, Color(0xFFEA580C)),
        Pair(AppThemeMode.DARK_NAVY, Color(0xFF0F172A)),
        Pair(AppThemeMode.SYSTEM_DARK, Color(0xFF334155))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Theme Selection Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "App Theme & Appearance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Select your preferred color scheme",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    themes.chunked(2).forEach { rowThemes ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowThemes.forEach { (mode, color) ->
                                val isSelected = currentTheme == mode
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onSelectTheme(mode) }
                                        .then(
                                            if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                                            else Modifier
                                        )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = mode.displayName,
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Language Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Language / भाषा",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FilterChip(
                        selected = currentLanguage == AppLanguage.ENGLISH,
                        onClick = { onSelectLanguage(AppLanguage.ENGLISH) },
                        label = { Text("English") },
                        shape = RoundedCornerShape(12.dp)
                    )
                    FilterChip(
                        selected = currentLanguage == AppLanguage.HINDI,
                        onClick = { onSelectLanguage(AppLanguage.HINDI) },
                        label = { Text("हिन्दी (Hindi)") },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }

        // Notifications Preferences
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Push & Reminder Notifications",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                SettingSwitchRow("Class Reminders", notificationPrefs.upcomingClass) {
                    onUpdateNotifications(notificationPrefs.copy(upcomingClass = it))
                }
                SettingSwitchRow("Homework Deadlines", notificationPrefs.homeworkDeadline) {
                    onUpdateNotifications(notificationPrefs.copy(homeworkDeadline = it))
                }
                SettingSwitchRow("Exam Reminders", notificationPrefs.examReminder) {
                    onUpdateNotifications(notificationPrefs.copy(examReminder = it))
                }
                SettingSwitchRow("Attendance Warnings", notificationPrefs.attendanceWarning) {
                    onUpdateNotifications(notificationPrefs.copy(attendanceWarning = it))
                }
            }
        }

        // Legal & Support Links Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                SettingsNavigationItem(Icons.Default.Info, "About StudyMate") {
                    onNavigate(AppDestination.ABOUT)
                }
                SettingsNavigationItem(Icons.Default.PrivacyTip, "Privacy Policy") {
                    onNavigate(AppDestination.PRIVACY_POLICY)
                }
                SettingsNavigationItem(Icons.Default.Description, "Terms & Conditions") {
                    onNavigate(AppDestination.TERMS)
                }
                SettingsNavigationItem(Icons.AutoMirrored.Filled.Help, "Help & Support") {
                    onNavigate(AppDestination.HELP_SUPPORT)
                }
            }
        }

        // Logout Button
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("settings_logout_button")
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout of Account", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SettingSwitchRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 14.sp)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingsNavigationItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(label, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
        }
    }
}
