package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.TaskCategory
import com.example.data.TaskItem
import com.example.data.TaskPriority
import com.example.speech.YolkVoiceLines
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context verifies Yolk app name`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Yolk", appName)
  }

  @Test
  fun `voice line generation creates encouraging 5-minute alerts`() {
    val alertLine = YolkVoiceLines.get5MinuteAlert("Budget Review")
    assertNotNull(alertLine)
    assertTrue("Alert must mention the task or 5 minutes", alertLine.contains("5 minutes") || alertLine.contains("Budget Review"))
    // Ensure response is short (under 250 characters, typically 1-2 sentences)
    assertTrue("Spoken alert should be concise", alertLine.length < 250)
  }

  @Test
  fun `task item creation has correct defaults`() {
    val task = TaskItem(
      title = "Stretch",
      dueTimestamp = System.currentTimeMillis() + 300_000,
      category = TaskCategory.HEALTH,
      priority = TaskPriority.HIGH
    )
    assertEquals("Stretch", task.title)
    assertEquals(TaskCategory.HEALTH, task.category)
    assertEquals(false, task.isCompleted)
    assertEquals(false, task.alert5MinTriggered)
  }
}
