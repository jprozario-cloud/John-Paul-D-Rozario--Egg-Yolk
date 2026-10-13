package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TaskCategory
import com.example.data.TaskPriority
import com.example.ui.theme.YolkCoral
import com.example.ui.theme.YolkOrange
import com.example.ui.theme.YolkWhite
import com.example.ui.theme.YolkYellowPrimary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTaskBottomSheet(
  onDismiss: () -> Unit,
  onAddTask: (title: String, desc: String, category: TaskCategory, priority: TaskPriority, dueTimestamp: Long) -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf(TaskCategory.WORK) }
  var selectedPriority by remember { mutableStateOf(TaskPriority.MEDIUM) }
  var offsetMinutes by remember { mutableIntStateOf(15) } // Default 15 min from now

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    modifier = Modifier.testTag("add_task_bottom_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 36.dp)
        .verticalScroll(rememberScrollState())
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Schedule a Task",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Yolk will alert you 5 minutes before!",
            style = MaterialTheme.typography.bodySmall,
            color = YolkOrange,
            fontWeight = FontWeight.SemiBold
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Title input
      OutlinedTextField(
        value = title,
        onValueChange = { title = it },
        label = { Text("Task Title (e.g., Team Standup, Drink Water)") },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("task_title_input"),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Description input
      OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text("Notes / Details (optional)") },
        maxLines = 2,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("task_description_input"),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Quick Time Selection
      Text(
        text = "When is it happening?",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(8.dp))

      val presets = listOf(
        5 to "In 5 min (Alert now!)",
        10 to "In 10 min",
        15 to "In 15 min",
        30 to "In 30 min",
        60 to "In 1 hour",
        120 to "In 2 hours"
      )

      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        presets.forEach { (mins, label) ->
          val isSelected = offsetMinutes == mins
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isSelected) YolkYellowPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier
              .clickable { offsetMinutes = mins }
              .testTag("preset_time_${mins}m")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              if (mins == 5) {
                Icon(
                  imageVector = Icons.Default.NotificationsActive,
                  contentDescription = null,
                  tint = if (isSelected) Color.White else YolkCoral,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
              }
              Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Category Chips
      Text(
        text = "Category",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(8.dp))

      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        TaskCategory.values().forEach { category ->
          val isSelected = selectedCategory == category
          FilterChip(
            selected = isSelected,
            onClick = { selectedCategory = category },
            label = {
              Text("${category.emoji} ${category.displayName}", fontSize = 12.sp)
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = category.color.copy(alpha = 0.2f),
              selectedLabelColor = category.color
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Priority
      Text(
        text = "Priority",
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TaskPriority.values().forEach { priority ->
          val isSelected = selectedPriority == priority
          FilterChip(
            selected = isSelected,
            onClick = { selectedPriority = priority },
            label = { Text(priority.label, fontSize = 12.sp) }
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Submit Button
      Button(
        onClick = {
          if (title.isNotBlank()) {
            val due = System.currentTimeMillis() + (offsetMinutes * 60 * 1000)
            onAddTask(title, description, selectedCategory, selectedPriority, due)
            onDismiss()
          }
        },
        enabled = title.isNotBlank(),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("submit_add_task_button"),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = YolkYellowPrimary)
      ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = YolkWhite)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Hatch Task & Set 5-Min Reminder",
          fontWeight = FontWeight.Bold,
          color = YolkWhite,
          fontSize = 15.sp
        )
      }
    }
  }
}
