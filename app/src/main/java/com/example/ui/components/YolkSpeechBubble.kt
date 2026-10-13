package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.rounded.Replay
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.YolkOrange
import com.example.ui.theme.YolkYellowPrimary

@Composable
fun YolkSpeechBubble(
  text: String,
  isSpeaking: Boolean,
  isMuted: Boolean,
  onReplay: () -> Unit,
  onToggleMute: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .shadow(6.dp, RoundedCornerShape(20.dp))
      .testTag("yolk_speech_bubble"),
    shape = RoundedCornerShape(20.dp),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 2.dp
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(YolkYellowPrimary),
            contentAlignment = Alignment.Center
          ) {
            Text(text = "🍳", fontSize = 13.sp)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Yolk says",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = YolkOrange
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          if (isSpeaking) {
            SoundWavesIndicator()
            Spacer(modifier = Modifier.width(6.dp))
          }

          IconButton(
            onClick = onReplay,
            modifier = Modifier
              .size(36.dp)
              .testTag("replay_speech_button")
          ) {
            Icon(
              imageVector = Icons.Rounded.Replay,
              contentDescription = "Replay Yolk's voice",
              modifier = Modifier.size(20.dp),
              tint = MaterialTheme.colorScheme.primary
            )
          }

          IconButton(
            onClick = onToggleMute,
            modifier = Modifier
              .size(36.dp)
              .testTag("toggle_mute_button")
          ) {
            Icon(
              imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
              contentDescription = if (isMuted) "Unmute voice" else "Mute voice",
              modifier = Modifier.size(20.dp),
              tint = if (isMuted) Color.Gray else MaterialTheme.colorScheme.primary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Medium,
        lineHeight = 22.sp,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.testTag("yolk_speech_text")
      )
    }
  }
}

@Composable
private fun SoundWavesIndicator() {
  val infiniteTransition = rememberInfiniteTransition(label = "wave_anim")
  val height1 by infiniteTransition.animateFloat(
    initialValue = 6f,
    targetValue = 18f,
    animationSpec = infiniteRepeatable(tween(250, easing = LinearEasing), RepeatMode.Reverse),
    label = "h1"
  )
  val height2 by infiniteTransition.animateFloat(
    initialValue = 18f,
    targetValue = 8f,
    animationSpec = infiniteRepeatable(tween(280, easing = LinearEasing), RepeatMode.Reverse),
    label = "h2"
  )
  val height3 by infiniteTransition.animateFloat(
    initialValue = 10f,
    targetValue = 20f,
    animationSpec = infiniteRepeatable(tween(220, easing = LinearEasing), RepeatMode.Reverse),
    label = "h3"
  )

  Row(
    horizontalArrangement = Arrangement.spacedBy(3.dp),
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.height(20.dp)
  ) {
    Box(
      modifier = Modifier
        .width(3.dp)
        .height(height1.dp)
        .clip(RoundedCornerShape(2.dp))
        .background(YolkYellowPrimary)
    )
    Box(
      modifier = Modifier
        .width(3.dp)
        .height(height2.dp)
        .clip(RoundedCornerShape(2.dp))
        .background(YolkOrange)
    )
    Box(
      modifier = Modifier
        .width(3.dp)
        .height(height3.dp)
        .clip(RoundedCornerShape(2.dp))
        .background(YolkYellowPrimary)
    )
  }
}
