package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.YolkAmber
import com.example.ui.theme.YolkCoral
import com.example.ui.theme.YolkDarkSurface
import com.example.ui.theme.YolkOrange
import com.example.ui.theme.YolkWhite
import com.example.ui.theme.YolkYellowPrimary
import com.example.viewmodel.EggAccessory
import com.example.viewmodel.YolkMood
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun YolkCharacter(
  modifier: Modifier = Modifier,
  sizeDp: Dp = 190.dp,
  isSpeaking: Boolean = false,
  mood: YolkMood = YolkMood.SUNNY,
  accessory: EggAccessory = EggAccessory.NONE,
  use3DPhoto: Boolean = false,
  onPoke: () -> Unit = {}
) {
  val coroutineScope = rememberCoroutineScope()
  val wobbleAngle = remember { Animatable(0f) }
  val scaleFactor = remember { Animatable(1f) }
  val interactionSource = remember { MutableInteractionSource() }

  // Idle gentle breathing animation
  val infiniteTransition = rememberInfiniteTransition(label = "yolk_breathing")
  val breathScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = if (isSpeaking) 1.05f else 1.02f,
    animationSpec = infiniteRepeatable(
      animation = tween(if (isSpeaking) 250 else 1200, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "breath"
  )

  // Periodic blinking state
  var isBlinking by remember { mutableStateOf(false) }
  LaunchedEffect(Unit) {
    while (isActive) {
      delay((3000..5000).random().toLong())
      isBlinking = true
      delay(180)
      isBlinking = false
    }
  }

  // Rapid speech wobble when talking
  LaunchedEffect(isSpeaking) {
    if (isSpeaking) {
      while (isActive && isSpeaking) {
        wobbleAngle.animateTo(
          targetValue = 4f,
          animationSpec = tween(120)
        )
        wobbleAngle.animateTo(
          targetValue = -4f,
          animationSpec = tween(120)
        )
      }
      wobbleAngle.animateTo(0f, spring(stiffness = Spring.StiffnessLow))
    }
  }

  Box(
    modifier = modifier
      .size(sizeDp)
      .scale(scaleFactor.value * breathScale)
      .rotate(wobbleAngle.value)
      .testTag("yolk_character_avatar")
      .clickable(
        interactionSource = interactionSource,
        indication = null
      ) {
        coroutineScope.launch {
          // Bouncy squish & tilt on tap!
          launch {
            scaleFactor.animateTo(0.90f, tween(80))
            scaleFactor.animateTo(1.10f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            scaleFactor.animateTo(1f, spring())
          }
          launch {
            wobbleAngle.animateTo(8f, tween(70))
            wobbleAngle.animateTo(-8f, tween(70))
            wobbleAngle.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
          }
        }
        onPoke()
      },
    contentAlignment = Alignment.Center
  ) {
    if (use3DPhoto) {
      // 3D Generated Mascot Portrait
      Surface(
        modifier = Modifier
          .fillMaxSize()
          .shadow(8.dp, CircleShape),
        shape = CircleShape,
        color = Color(0xFFFFF6E5)
      ) {
        Image(
          painter = painterResource(id = R.drawable.img_yolk_avatar),
          contentDescription = "Yolk Companion Mascot",
          modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape)
        )
      }
    } else {
      // Canvas Animated Vector Egg Character
      Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Soft drop shadow at base
        drawOval(
          color = Color(0x22000000),
          topLeft = Offset(w * 0.18f, h * 0.88f),
          size = Size(w * 0.64f, h * 0.12f)
        )

        // Egg Shell Body (Top narrower, bottom wider)
        val eggPath = Path().apply {
          moveTo(w * 0.5f, h * 0.08f)
          // Top right curve down to right hip
          cubicTo(
            w * 0.82f, h * 0.10f,
            w * 0.96f, h * 0.58f,
            w * 0.88f, h * 0.84f
          )
          // Bottom curve
          cubicTo(
            w * 0.80f, h * 0.98f,
            w * 0.20f, h * 0.98f,
            w * 0.12f, h * 0.84f
          )
          // Left hip curve back up to top
          cubicTo(
            w * 0.04f, h * 0.58f,
            w * 0.18f, h * 0.10f,
            w * 0.5f, h * 0.08f
          )
          close()
        }

        // Egg Shell Gradient Fill
        drawPath(
          path = eggPath,
          brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFFFFDF8), Color(0xFFFFF3D0), Color(0xFFFFECC0)),
            startY = 0f,
            endY = h
          )
        )

        // Egg Shell Outline
        drawPath(
          path = eggPath,
          color = Color(0xFFE2C98D),
          style = Stroke(width = 3.5.dp.toPx())
        )

        // Sunny Golden Yolk Tummy
        val yolkCenterX = w * 0.5f
        val yolkCenterY = h * 0.65f
        val yolkRadius = w * 0.26f

        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(YolkAmber, YolkYellowPrimary, YolkOrange),
            center = Offset(yolkCenterX - yolkRadius * 0.2f, yolkCenterY - yolkRadius * 0.2f),
            radius = yolkRadius * 1.2f
          ),
          radius = yolkRadius,
          center = Offset(yolkCenterX, yolkCenterY)
        )

        // Yolk Shine Highlight
        drawOval(
          color = Color(0x88FFFFFF),
          topLeft = Offset(yolkCenterX - yolkRadius * 0.55f, yolkCenterY - yolkRadius * 0.65f),
          size = Size(yolkRadius * 0.50f, yolkRadius * 0.30f)
        )

        // Rosy Cheeks
        drawOval(
          color = YolkCoral.copy(alpha = 0.45f),
          topLeft = Offset(w * 0.24f, h * 0.46f),
          size = Size(w * 0.11f, h * 0.06f)
        )
        drawOval(
          color = YolkCoral.copy(alpha = 0.45f),
          topLeft = Offset(w * 0.65f, h * 0.46f),
          size = Size(w * 0.11f, h * 0.06f)
        )

        // Eyes
        val eyeLeftX = w * 0.36f
        val eyeRightX = w * 0.64f
        val eyeY = h * 0.42f
        val eyeRadius = w * 0.048f

        if (isBlinking) {
          // Closed happy eye curves (^_^)
          drawArc(
            color = YolkDarkSurface,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(eyeLeftX - eyeRadius, eyeY - eyeRadius * 0.6f),
            size = Size(eyeRadius * 2, eyeRadius * 1.2f),
            style = Stroke(width = 3.dp.toPx())
          )
          drawArc(
            color = YolkDarkSurface,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(eyeRightX - eyeRadius, eyeY - eyeRadius * 0.6f),
            size = Size(eyeRadius * 2, eyeRadius * 1.2f),
            style = Stroke(width = 3.dp.toPx())
          )
        } else {
          // Open sparkling big cartoon eyes
          drawCircle(
            color = YolkDarkSurface,
            radius = eyeRadius,
            center = Offset(eyeLeftX, eyeY)
          )
          drawCircle(
            color = YolkWhite,
            radius = eyeRadius * 0.42f,
            center = Offset(eyeLeftX - eyeRadius * 0.3f, eyeY - eyeRadius * 0.3f)
          )
          drawCircle(
            color = YolkWhite,
            radius = eyeRadius * 0.20f,
            center = Offset(eyeLeftX + eyeRadius * 0.25f, eyeY + eyeRadius * 0.25f)
          )

          drawCircle(
            color = YolkDarkSurface,
            radius = eyeRadius,
            center = Offset(eyeRightX, eyeY)
          )
          drawCircle(
            color = YolkWhite,
            radius = eyeRadius * 0.42f,
            center = Offset(eyeRightX - eyeRadius * 0.3f, eyeY - eyeRadius * 0.3f)
          )
          drawCircle(
            color = YolkWhite,
            radius = eyeRadius * 0.20f,
            center = Offset(eyeRightX + eyeRadius * 0.25f, eyeY + eyeRadius * 0.25f)
          )
        }

        // Animated Mouth
        val mouthX = w * 0.5f
        val mouthY = h * 0.50f
        if (isSpeaking) {
          // Flapping animated open mouth (O shape with pink tongue)
          drawOval(
            color = YolkDarkSurface,
            topLeft = Offset(mouthX - w * 0.07f, mouthY - h * 0.02f),
            size = Size(w * 0.14f, h * 0.08f)
          )
          drawOval(
            color = YolkCoral,
            topLeft = Offset(mouthX - w * 0.045f, mouthY + h * 0.015f),
            size = Size(w * 0.09f, h * 0.04f)
          )
        } else {
          // Cheerful open smile
          val mouthPath = Path().apply {
            moveTo(mouthX - w * 0.07f, mouthY)
            quadraticTo(mouthX, mouthY + h * 0.06f, mouthX + w * 0.07f, mouthY)
          }
          drawPath(
            path = mouthPath,
            color = YolkDarkSurface,
            style = Stroke(width = 3.dp.toPx())
          )
        }

        // Render Accessory Over Egg
        drawAccessory(accessory, w, h)
      }
    }

    // Mood Badge at Bottom-Right
    Surface(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .offset(x = (-6).dp, y = (-6).dp),
      shape = CircleShape,
      color = MaterialTheme.colorScheme.primaryContainer,
      shadowElevation = 3.dp
    ) {
      Text(
        text = mood.emoji,
        fontSize = 18.sp,
        modifier = Modifier.padding(5.dp)
      )
    }
  }
}

private fun DrawScope.drawAccessory(accessory: EggAccessory, w: Float, h: Float) {
  when (accessory) {
    EggAccessory.CROWN -> {
      // Golden Crown
      val crownPath = Path().apply {
        moveTo(w * 0.32f, h * 0.12f)
        lineTo(w * 0.30f, h * 0.02f)
        lineTo(w * 0.42f, h * 0.07f)
        lineTo(w * 0.50f, h * 0.00f)
        lineTo(w * 0.58f, h * 0.07f)
        lineTo(w * 0.70f, h * 0.02f)
        lineTo(w * 0.68f, h * 0.12f)
        close()
      }
      drawPath(path = crownPath, color = YolkAmber, style = Fill)
      drawPath(path = crownPath, color = YolkOrange, style = Stroke(width = 2.dp.toPx()))
      // Ruby jewel in center
      drawCircle(color = YolkCoral, radius = w * 0.025f, center = Offset(w * 0.50f, h * 0.08f))
    }
    EggAccessory.SHADES -> {
      // Cool Sunglasses
      val shadesY = h * 0.40f
      drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(w * 0.22f, shadesY),
        size = Size(w * 0.25f, h * 0.08f),
        cornerRadius = CornerRadius(10f, 10f)
      )
      drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(w * 0.53f, shadesY),
        size = Size(w * 0.25f, h * 0.08f),
        cornerRadius = CornerRadius(10f, 10f)
      )
      // Bridge
      drawRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(w * 0.46f, shadesY + h * 0.02f),
        size = Size(w * 0.08f, h * 0.03f)
      )
      // Glare reflection on glasses
      drawLine(
        color = Color(0x66FFFFFF),
        start = Offset(w * 0.25f, shadesY + h * 0.06f),
        end = Offset(w * 0.35f, shadesY + h * 0.02f),
        strokeWidth = 2.dp.toPx()
      )
      drawLine(
        color = Color(0x66FFFFFF),
        start = Offset(w * 0.56f, shadesY + h * 0.06f),
        end = Offset(w * 0.66f, shadesY + h * 0.02f),
        strokeWidth = 2.dp.toPx()
      )
    }
    EggAccessory.CHEF -> {
      // Chef Hat
      val chefPath = Path().apply {
        moveTo(w * 0.35f, h * 0.10f)
        lineTo(w * 0.33f, h * 0.03f)
        cubicTo(w * 0.22f, -h * 0.06f, w * 0.42f, -h * 0.09f, w * 0.50f, -h * 0.06f)
        cubicTo(w * 0.58f, -h * 0.09f, w * 0.78f, -h * 0.06f, w * 0.67f, h * 0.03f)
        lineTo(w * 0.65f, h * 0.10f)
        close()
      }
      drawPath(path = chefPath, color = YolkWhite, style = Fill)
      drawPath(path = chefPath, color = Color(0xFFCBD5E1), style = Stroke(width = 2.dp.toPx()))
      // Hat band
      drawRoundRect(
        color = YolkCoral,
        topLeft = Offset(w * 0.33f, h * 0.08f),
        size = Size(w * 0.34f, h * 0.035f),
        cornerRadius = CornerRadius(4f, 4f)
      )
    }
    EggAccessory.PARTY -> {
      // Party Cone
      val conePath = Path().apply {
        moveTo(w * 0.48f, -h * 0.08f)
        lineTo(w * 0.38f, h * 0.11f)
        lineTo(w * 0.62f, h * 0.09f)
        close()
      }
      drawPath(path = conePath, color = Color(0xFF8B5CF6), style = Fill)
      drawPath(path = conePath, color = Color(0xFF6D28D9), style = Stroke(width = 2.dp.toPx()))
      // Pom-pom on top
      drawCircle(color = YolkAmber, radius = w * 0.035f, center = Offset(w * 0.48f, -h * 0.08f))
    }
    EggAccessory.NONE -> {}
  }
}
