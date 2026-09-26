package com.example.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.viewmodel.AppDestination

sealed class BottomNavItem(val destination: AppDestination, val label: String, val icon: ImageVector) {
    object Home : BottomNavItem(AppDestination.DASHBOARD, "Home", Icons.Default.Home)
    object Timetable : BottomNavItem(AppDestination.TIMETABLE, "Timetable", Icons.Default.CalendarToday)
    object Tasks : BottomNavItem(AppDestination.HOMEWORK, "Tasks", Icons.Default.Assignment)
    object Notes : BottomNavItem(AppDestination.NOTES, "Notes", Icons.Default.MenuBook)
    object Profile : BottomNavItem(AppDestination.PROFILE, "Profile", Icons.Default.Person)
}

@Composable
fun StudyMateBottomBar(
    currentDestination: AppDestination,
    onNavigate: (AppDestination) -> Unit
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Timetable,
        BottomNavItem.Tasks,
        BottomNavItem.Notes,
        BottomNavItem.Profile
    )

    NavigationBar(
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentDestination == item.destination
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(item.label) },
                selected = isSelected,
                onClick = { onNavigate(item.destination) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.testTag("bottom_nav_${item.label.lowercase()}")
            )
        }
    }
}
