package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TaskItem
import com.example.ui.theme.YolkCoral
import com.example.ui.theme.YolkGreenSuccess
import com.example.ui.theme.YolkOrange
import com.example.ui.theme.YolkYellowDark
import com.example.ui.theme.YolkYellowPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun YolkTaskCard(
  task: TaskItem,
  currentTimeMillis: Long,
  onCompleteToggle: (Boolean) -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val diffMillis = task.dueTimestamp - currentTimeMillis
  val isUpcomingWithin5Min = !task.isCompleted && diffMillis in 0..(5 * 60 * 1000)
  val isOverdue = !task.isCompleted && diffMillis < 0

  val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
  val timeString = timeFormatter.format(Date(task.dueTimestamp))

  val remainingText = when {
    task.isCompleted -> "Completed"
    isOverdue -> "Started ${formatDuration(-diffMillis)} ago"
    diffMillis <= 60_000 -> "Starts in less than a minute!"
    diffMillis <= 5 * 60_000 -> "Starts in ${formatDuration(diffMillis)}"
    else -> "In ${formatDuration(diffMillis)}"
  }

  // Border glow when in 5-minute alert window
  val borderStroke = if (isUpcomingWithin5Min) {
    BorderStroke(2.dp, YolkCoral)
  } else if (!task.isCompleted && diffMillis <= 15 * 60_000) {
    BorderStroke(1.5.dp, YolkYellowPrimary)
  } else {
    BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("task_card_${task.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (task.isCompleted) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
      } else if (isUpcomingWithin5Min) {
        YolkCoral.copy(alpha = 0.06f)
      } else {
        MaterialTheme.colorScheme.surface
      }
    ),
    border = borderStroke,
    elevation = CardDefaults.cardElevation(defaultElevation = if (isUpcomingWithin5Min) 4.dp else 1.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Category and Priority Badges
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = task.category.color.copy(alpha = 0.15f)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = task.category.emoji, fontSize = 12.sp)
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = task.category.displayName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = task.category.color
              )
            }
          }

          if (isUpcomingWithin5Min) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = YolkCoral
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.NotificationsActive,
                  contentDescription = "5-min alert active",
                  tint = Color.White,
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "5-MIN ALERT",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Black,
                  color = Color.White
                )
              }
            }
          }
        }

        // Delete button
        IconButton(
          onClick = onDelete,
          modifier = Modifier
            .size(32.dp)
            .testTag("delete_task_button_${task.id}")
        ) {
          Icon(
            imageVector = Icons.Default.DeleteOutline,
            contentDescription = "Delete task",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Title & Completion Checkbox
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          onClick = { onCompleteToggle(!task.isCompleted) },
          shape = CircleShape,
          color = if (task.isCompleted) YolkGreenSuccess else Color.Transparent,
          border = BorderStroke(
            2.dp,
            if (task.isCompleted) YolkGreenSuccess else MaterialTheme.colorScheme.outline
          ),
          modifier = Modifier
            .size(28.dp)
            .testTag("complete_checkbox_${task.id}")
        ) {
          if (task.isCompleted) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Completed",
              tint = Color.White,
              modifier = Modifier.padding(4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = task.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
            color = if (task.isCompleted) {
              MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            } else {
              MaterialTheme.colorScheme.onSurface
            }
          )

          if (task.description.isNotBlank()) {
            Text(
              text = task.description,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 2
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Time & Countdown Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Schedule,
            contentDescription = null,
            tint = if (isUpcomingWithin5Min) YolkCoral else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = timeString,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Text(
          text = remainingText,
          style = MaterialTheme.typography.labelMedium,
          fontWeight = if (isUpcomingWithin5Min) FontWeight.ExtraBold else FontWeight.Medium,
          color = when {
            task.isCompleted -> YolkGreenSuccess
            isUpcomingWithin5Min -> YolkCoral
            isOverdue -> MaterialTheme.colorScheme.error
            else -> YolkYellowDark
          }
        )
      }
    }
  }
}

private fun formatDuration(millis: Long): String {
  val totalSeconds = millis / 1000
  val minutes = (totalSeconds % 3600) / 60
  val hours = totalSeconds / 3600
  val seconds = totalSeconds % 60

  return when {
    hours > 0 -> "${hours}h ${minutes}m"
    minutes > 0 -> "${minutes}m ${seconds}s"
    else -> "${seconds}s"
  }
}
