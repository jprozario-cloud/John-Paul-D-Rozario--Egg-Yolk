package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.TaskItem
import com.example.ui.theme.YolkCoral
import com.example.ui.theme.YolkGreenSuccess
import com.example.ui.theme.YolkOrange
import com.example.ui.theme.YolkWhite
import com.example.ui.theme.YolkYellowPrimary

@Composable
fun YolkAlertDialog(
  task: TaskItem,
  spokenText: String,
  onDismiss: () -> Unit,
  onSnooze: () -> Unit,
  onComplete: () -> Unit,
  onReplayVoice: () -> Unit
) {
  val infiniteTransition = rememberInfiniteTransition(label = "bell_pulse")
  val bellAngle by infiniteTransition.animateFloat(
    initialValue = -18f,
    targetValue = 18f,
    animationSpec = infiniteRepeatable(
      animation = tween(140),
      repeatMode = RepeatMode.Reverse
    ),
    label = "bell_tilt"
  )
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.08f,
    animationSpec = infiniteRepeatable(
      animation = tween(400),
      repeatMode = RepeatMode.Reverse
    ),
    label = "bell_scale"
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
  ) {
    Surface(
      shape = RoundedCornerShape(28.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 8.dp,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("yolk_5min_alert_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Bell and Yolk Alert Art
        Box(
          modifier = Modifier.size(130.dp),
          contentAlignment = Alignment.Center
        ) {
          Image(
            painter = painterResource(id = R.drawable.img_yolk_alert),
            contentDescription = "Yolk Alert Illustration",
            modifier = Modifier
              .size(115.dp)
              .clip(CircleShape)
          )

          // Ringing Bell Badge
          Surface(
            modifier = Modifier
              .align(Alignment.TopEnd)
              .rotate(bellAngle)
              .scale(pulseScale),
            shape = CircleShape,
            color = YolkCoral,
            shadowElevation = 6.dp
          ) {
            Icon(
              imageVector = Icons.Default.NotificationsActive,
              contentDescription = "5-Min Alarm",
              tint = Color.White,
              modifier = Modifier
                .padding(8.dp)
                .size(22.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Headline
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = YolkCoral.copy(alpha = 0.12f)
        ) {
          Text(
            text = "⏰ 5 MINUTES REMAINING!",
            color = YolkCoral,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = task.title,
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.ExtraBold,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Yolk Speech Card
        Surface(
          shape = RoundedCornerShape(18.dp),
          color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "🍳", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Yolk says:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = YolkOrange
              )
              Text(
                text = "\"$spokenText\"",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
            IconButton(
              onClick = onReplayVoice,
              modifier = Modifier.size(36.dp)
            ) {
              Icon(
                imageVector = Icons.Rounded.VolumeUp,
                contentDescription = "Hear Yolk speak",
                tint = YolkOrange
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Actions
        Button(
          onClick = onDismiss,
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("alert_ready_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = YolkYellowPrimary)
        ) {
          Text(
            text = "I'm Ready! Let's Go!",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = YolkWhite
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = onSnooze,
            modifier = Modifier
              .weight(1f)
              .height(46.dp)
              .testTag("alert_snooze_button"),
            shape = RoundedCornerShape(14.dp)
          ) {
            Icon(imageVector = Icons.Default.Snooze, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Snooze 2m", fontSize = 13.sp)
          }

          FilledTonalButton(
            onClick = onComplete,
            modifier = Modifier
              .weight(1f)
              .height(46.dp)
              .testTag("alert_complete_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
              containerColor = YolkGreenSuccess.copy(alpha = 0.2f),
              contentColor = YolkGreenSuccess
            )
          ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Done Already!", fontSize = 13.sp)
          }
        }
      }
    }
  }
}
