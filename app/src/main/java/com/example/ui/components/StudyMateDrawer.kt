package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.User
import com.example.model.UserRole
import com.example.ui.theme.StudyMateGradient
import com.example.viewmodel.AppDestination

@Composable
fun StudyMateDrawerContent(
    currentUser: User?,
    currentDestination: AppDestination,
    onNavigate: (AppDestination) -> Unit,
    onLogout: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.widthIn(max = 320.dp),
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(StudyMateGradient)
                    .padding(24.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser?.name?.take(1)?.uppercase() ?: "S",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = currentUser?.name ?: "Student",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = currentUser?.email ?: "student@studymate.com",
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        fontSize = 13.sp
                    )
                    if (currentUser?.role == UserRole.ADMIN) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Text(
                                text = "ADMINISTRATOR",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main navigation items
            DrawerItem("Dashboard", Icons.Default.Dashboard, currentDestination == AppDestination.DASHBOARD) {
                onNavigate(AppDestination.DASHBOARD)
            }
            DrawerItem("Timetable", Icons.Default.Schedule, currentDestination == AppDestination.TIMETABLE) {
                onNavigate(AppDestination.TIMETABLE)
            }
            DrawerItem("Notes", Icons.Default.MenuBook, currentDestination == AppDestination.NOTES) {
                onNavigate(AppDestination.NOTES)
            }
            DrawerItem("Homework & Tasks", Icons.Default.Assignment, currentDestination == AppDestination.HOMEWORK) {
                onNavigate(AppDestination.HOMEWORK)
            }
            DrawerItem("Exams", Icons.Default.School, currentDestination == AppDestination.EXAMS) {
                onNavigate(AppDestination.EXAMS)
            }
            DrawerItem("Study Goals", Icons.Default.Flag, currentDestination == AppDestination.GOALS) {
                onNavigate(AppDestination.GOALS)
            }
            DrawerItem("Study Timer", Icons.Default.Timer, currentDestination == AppDestination.TIMER) {
                onNavigate(AppDestination.TIMER)
            }
            DrawerItem("Attendance", Icons.Default.FactCheck, currentDestination == AppDestination.ATTENDANCE) {
                onNavigate(AppDestination.ATTENDANCE)
            }
            DrawerItem("Calendar", Icons.Default.CalendarMonth, currentDestination == AppDestination.CALENDAR) {
                onNavigate(AppDestination.CALENDAR)
            }
            DrawerItem("Progress Tracker", Icons.Default.BarChart, currentDestination == AppDestination.PROGRESS) {
                onNavigate(AppDestination.PROGRESS)
            }

            if (currentUser?.role == UserRole.ADMIN) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))
                DrawerItem("Admin Panel", Icons.Default.AdminPanelSettings, currentDestination == AppDestination.ADMIN_PANEL) {
                    onNavigate(AppDestination.ADMIN_PANEL)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))

            DrawerItem("Settings", Icons.Default.Settings, currentDestination == AppDestination.SETTINGS) {
                onNavigate(AppDestination.SETTINGS)
            }
            DrawerItem("About StudyMate", Icons.Default.Info, currentDestination == AppDestination.ABOUT) {
                onNavigate(AppDestination.ABOUT)
            }
            DrawerItem("Privacy Policy", Icons.Default.PrivacyTip, currentDestination == AppDestination.PRIVACY_POLICY) {
                onNavigate(AppDestination.PRIVACY_POLICY)
            }
            DrawerItem("Terms & Conditions", Icons.Default.Description, currentDestination == AppDestination.TERMS) {
                onNavigate(AppDestination.TERMS)
            }
            DrawerItem("Help & Support", Icons.AutoMirrored.Filled.Help, currentDestination == AppDestination.HELP_SUPPORT) {
                onNavigate(AppDestination.HELP_SUPPORT)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp))

            NavigationDrawerItem(
                label = { Text("Logout", color = MaterialTheme.colorScheme.error) },
                icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = MaterialTheme.colorScheme.error) },
                selected = false,
                onClick = onLogout,
                modifier = Modifier
                    .padding(NavigationDrawerItemDefaults.ItemPadding)
                    .testTag("drawer_logout_button")
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DrawerItem(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(title, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
        icon = { Icon(icon, contentDescription = title) },
        selected = selected,
        onClick = onClick,
        modifier = Modifier
            .padding(NavigationDrawerItemDefaults.ItemPadding)
            .testTag("drawer_${title.lowercase().replace(" ", "_")}")
    )
}
