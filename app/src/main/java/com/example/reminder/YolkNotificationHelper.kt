package com.example.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.example.MainActivity

class YolkNotificationHelper(private val context: Context) {
  companion object {
    const val CHANNEL_ID = "yolk_task_alerts_channel"
    const val CHANNEL_NAME = "Yolk 5-Minute Task Alerts"
  }

  private val notificationManager =
    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

  init {
    createChannel()
  }

  private fun createChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID,
        CHANNEL_NAME,
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Alerts sent by Yolk exactly 5 minutes before scheduled tasks"
        enableVibration(true)
        vibrationPattern = longArrayOf(0, 150, 100, 250, 100, 350)
      }
      notificationManager.createNotificationChannel(channel)
    }
  }

  fun show5MinuteAlertNotification(taskId: Long, taskTitle: String, spokenText: String) {
    val intent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      putExtra("ALERT_TASK_ID", taskId)
    }

    val pendingIntent = PendingIntent.getActivity(
      context,
      taskId.toInt(),
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(android.R.drawable.ic_dialog_info)
      .setContentTitle("🍳 Yolk: 5-Minute Alert!")
      .setContentText("'$taskTitle' starts in 5 minutes! Let's crush it!")
      .setStyle(NotificationCompat.BigTextStyle().bigText(spokenText))
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setAutoCancel(true)
      .setContentIntent(pendingIntent)
      .build()

    notificationManager.notify(taskId.toInt(), notification)
    vibrateAlert()
  }

  fun vibratePoke() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator?.vibrate(
          VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
        )
      } else {
        @Suppress("DEPRECATION")
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        vibrator?.vibrate(
          VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE)
        )
      }
    } catch (_: Exception) {}
  }

  fun vibrateAlert() {
    try {
      val pattern = longArrayOf(0, 150, 100, 250, 100, 350)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
          (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
        } else {
          @Suppress("DEPRECATION")
          context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
      }
    } catch (_: Exception) {}
  }
}
