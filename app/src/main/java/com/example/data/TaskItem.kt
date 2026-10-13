package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.YolkBlueFocus
import com.example.ui.theme.YolkCoral
import com.example.ui.theme.YolkGreenSuccess
import com.example.ui.theme.YolkOrange
import com.example.ui.theme.YolkPinkCreative
import com.example.ui.theme.YolkPurpleStudy

enum class TaskCategory(val displayName: String, val emoji: String, val color: Color) {
  WORK("Work", "💼", YolkOrange),
  FITNESS("Egg-cise", "💪", YolkGreenSuccess),
  STUDY("Study", "📚", YolkPurpleStudy),
  LIFE("Daily Life", "🌱", YolkBlueFocus),
  HEALTH("Water & Health", "💧", YolkCoral),
  CREATIVE("Creative", "🎨", YolkPinkCreative)
}

enum class TaskPriority(val label: String, val color: Color) {
  HIGH("Urgent", YolkCoral),
  MEDIUM("Standard", YolkOrange),
  LOW("Casual", YolkGreenSuccess)
}

data class TaskItem(
  val id: Long = 0,
  val title: String,
  val description: String = "",
  val category: TaskCategory = TaskCategory.WORK,
  val priority: TaskPriority = TaskPriority.MEDIUM,
  val dueTimestamp: Long,
  val isCompleted: Boolean = false,
  val alert5MinTriggered: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
)
