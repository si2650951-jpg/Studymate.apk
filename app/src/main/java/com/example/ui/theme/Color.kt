package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// StudyMate Primary Brand Colors
val StudyMateBlue = Color(0xFF2563EB)
val StudyMatePurple = Color(0xFF7C3AED)
val StudyMateNavy = Color(0xFF0F172A)
val StudyMateNavySurface = Color(0xFF1E293B)
val StudyMateNavyCard = Color(0xFF334155)

// Accent Colors
val AccentCyan = Color(0xFF06B6D4)
val AccentEmerald = Color(0xFF10B981)
val AccentAmber = Color(0xFFF59E0B)
val AccentRose = Color(0xFFF43F5E)
val AccentViolet = Color(0xFF8B5CF6)
val AccentOrange = Color(0xFFF97316)

// Theme Colors - Blue
val BluePrimary = Color(0xFF2563EB)
val BlueOnPrimary = Color(0xFFFFFFFF)
val BluePrimaryContainer = Color(0xFFDBEAFE)
val BlueOnPrimaryContainer = Color(0xFF1E40AF)
val BlueSecondary = Color(0xFF4F46E5)
val BlueBackground = Color(0xFFF8FAFC)
val BlueSurface = Color(0xFFFFFFFF)

// Theme Colors - Purple
val PurplePrimary = Color(0xFF7C3AED)
val PurpleOnPrimary = Color(0xFFFFFFFF)
val PurplePrimaryContainer = Color(0xFFEDE9FE)
val PurpleOnPrimaryContainer = Color(0xFF5B21B6)
val PurpleSecondary = Color(0xFF9333EA)
val PurpleBackground = Color(0xFFFAF5FF)
val PurpleSurface = Color(0xFFFFFFFF)

// Theme Colors - Green
val GreenPrimary = Color(0xFF059669)
val GreenOnPrimary = Color(0xFFFFFFFF)
val GreenPrimaryContainer = Color(0xFFD1FAE5)
val GreenOnPrimaryContainer = Color(0xFF065F46)
val GreenSecondary = Color(0xFF0D9488)
val GreenBackground = Color(0xFFF0FDF4)
val GreenSurface = Color(0xFFFFFFFF)

// Theme Colors - Orange
val OrangePrimary = Color(0xFFEA580C)
val OrangeOnPrimary = Color(0xFFFFFFFF)
val OrangePrimaryContainer = Color(0xFFFFEDD5)
val OrangeOnPrimaryContainer = Color(0xFF9A3412)
val OrangeSecondary = Color(0xFFF59E0B)
val OrangeBackground = Color(0xFFFFF7ED)
val OrangeSurface = Color(0xFFFFFFFF)

// Theme Colors - Dark Navy
val DarkNavyPrimary = Color(0xFF60A5FA)
val DarkNavyOnPrimary = Color(0xFF0F172A)
val DarkNavyPrimaryContainer = Color(0xFF1E3A8A)
val DarkNavyOnPrimaryContainer = Color(0xFFDBEAFE)
val DarkNavySecondary = Color(0xFFA78BFA)
val DarkNavyBackground = Color(0xFF0B0F19)
val DarkNavySurface = Color(0xFF131B2E)
val DarkNavySurfaceVariant = Color(0xFF1E293B)
val DarkNavyOnSurface = Color(0xFFF1F5F9)

// Theme Colors - Dark Mode
val DarkPrimary = Color(0xFFA78BFA)
val DarkOnPrimary = Color(0xFF1E1B4B)
val DarkPrimaryContainer = Color(0xFF312E81)
val DarkOnPrimaryContainer = Color(0xFFEDE9FE)
val DarkSecondary = Color(0xFF818CF8)
val DarkBackground = Color(0xFF0F172A)
val DarkSurface = Color(0xFF1E293B)
val DarkSurfaceVariant = Color(0xFF334155)
val DarkOnSurface = Color(0xFFF8FAFC)

// Soft Gradients
val StudyMateGradient = Brush.linearGradient(
    colors = listOf(StudyMateBlue, StudyMatePurple)
)

val NavyGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF312E81))
)

val CardGradients = listOf(
    Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF6366F1))),
    Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFFEC4899))),
    Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF06B6D4))),
    Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFEF4444)))
)
