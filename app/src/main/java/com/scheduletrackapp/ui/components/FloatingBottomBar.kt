package com.scheduletrackapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.ViewWeek
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.scheduletrackapp.Screen

private val CardWhite = Color(0xFFFFFFFF)
private val TextMain = Color(0xFF1E293B)
private val TextMuted = Color(0xFF94A3B8)
private val ActivePillYellow = Color(0xFFFACC15)

@Composable
fun FloatingBottomBar(
    currentScreen: Screen = Screen.KANBAN,
    onKanbanClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp, start = 24.dp, end = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = CardWhite,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 1: Kanban Board
                BottomBarItem(
                    icon = Icons.Outlined.ViewWeek,
                    contentDescription = "Kanban Board",
                    isSelected = currentScreen == Screen.KANBAN,
                    onClick = onKanbanClick
                )

                // Tab 2: Notifications
                BottomBarItem(
                    icon = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    isSelected = currentScreen == Screen.NOTIFICATION,
                    onClick = onNotificationClick
                )
            }
        }
    }
}

@Composable
private fun BottomBarItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // 🎬 Animasi Bouncy Scale pada Ikon Aktif
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "IconScale"
    )

    // 🎬 Animasi Transisi Kejelasan Lingkaran Kuning (Active Pill)
    val pillAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1.0f else 0.0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "PillAlpha"
    )

    // 🎬 Animasi Perubahan Warna Ikon (Muted -> Main Text)
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) TextMain else TextMuted,
        label = "IconColor"
    )

    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(ActivePillYellow.copy(alpha = pillAlpha))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconColor,
            modifier = Modifier
                .size(22.dp)
                .scale(scale)
        )
    }
}