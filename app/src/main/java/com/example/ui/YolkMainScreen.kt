package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Egg
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TaskCategory
import com.example.data.TaskItem
import com.example.speech.YolkVoiceLines
import com.example.ui.components.AddTaskBottomSheet
import com.example.ui.components.EggClosetScreen
import com.example.ui.components.YolkAlertDialog
import com.example.ui.components.YolkCharacter
import com.example.ui.components.YolkSpeechBubble
import com.example.ui.components.YolkTaskCard
import com.example.ui.theme.YolkAmber
import com.example.ui.theme.YolkCoral
import com.example.ui.theme.YolkOrange
import com.example.ui.theme.YolkWhite
import com.example.ui.theme.YolkYellowPrimary
import com.example.viewmodel.YolkViewModel

enum class MainTab(val title: String) {
  COMPANION("Companion"),
  TASKS("Tasks"),
  CLOSET("Egg Closet")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YolkMainScreen(
  viewModel: YolkViewModel,
  modifier: Modifier = Modifier
) {
  val tasks by viewModel.allTasks.collectAsState()
  val currentTime by viewModel.currentTimeMillis.collectAsState()
  val currentAlertTask by viewModel.currentAlertTask.collectAsState()
  val yolkMood by viewModel.yolkMood.collectAsState()
  val equippedAccessory by viewModel.equippedAccessory.collectAsState()
  val yolksEarned by viewModel.yolksEarned.collectAsState()
  val unlockedAccessories by viewModel.unlockedAccessories.collectAsState()
  val isSpeaking by viewModel.speechEngine.isSpeaking.collectAsState()
  val isMuted by viewModel.speechEngine.isMuted.collectAsState()
  val spokenText by viewModel.speechEngine.lastSpokenText.collectAsState()

  var selectedTab by remember { mutableStateOf(MainTab.COMPANION) }
  var showAddTaskSheet by remember { mutableStateOf(false) }
  var use3DAvatar by remember { mutableStateOf(false) }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = MaterialTheme.colorScheme.background,
    topBar = {
      CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = CircleShape,
              color = YolkYellowPrimary,
              modifier = Modifier.size(32.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(text = "🍳", fontSize = 18.sp)
              }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Yolk",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "5-Min Task Alert Companion",
                style = MaterialTheme.typography.labelSmall,
                color = YolkOrange,
                fontSize = 10.sp
              )
            }
          }
        },
        actions = {
          // Poke button
          IconButton(
            onClick = { viewModel.pokeYolk() },
            modifier = Modifier.testTag("top_poke_button")
          ) {
            Icon(
              imageVector = Icons.Default.TouchApp,
              contentDescription = "Poke Yolk",
              tint = YolkOrange
            )
          }

          // Mute toggle button
          IconButton(
            onClick = { viewModel.speechEngine.toggleMute() },
            modifier = Modifier.testTag("top_mute_button")
          ) {
            Icon(
              imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
              contentDescription = if (isMuted) "Unmute voice" else "Mute voice",
              tint = if (isMuted) Color.Gray else YolkYellowPrimary
            )
          }
        }
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("bottom_nav_bar")
      ) {
        NavigationBarItem(
          selected = selectedTab == MainTab.COMPANION,
          onClick = { selectedTab = MainTab.COMPANION },
          icon = {
            Icon(
              imageVector = if (selectedTab == MainTab.COMPANION) Icons.Filled.Egg else Icons.Outlined.Egg,
              contentDescription = "Companion"
            )
          },
          label = { Text("Yolk") },
          modifier = Modifier.testTag("nav_tab_companion")
        )
        NavigationBarItem(
          selected = selectedTab == MainTab.TASKS,
          onClick = { selectedTab = MainTab.TASKS },
          icon = {
            Icon(
              imageVector = if (selectedTab == MainTab.TASKS) Icons.Filled.FormatListBulleted else Icons.Outlined.FormatListBulleted,
              contentDescription = "Tasks"
            )
          },
          label = { Text("Tasks (${tasks.count { !it.isCompleted }})") },
          modifier = Modifier.testTag("nav_tab_tasks")
        )
        NavigationBarItem(
          selected = selectedTab == MainTab.CLOSET,
          onClick = { selectedTab = MainTab.CLOSET },
          icon = {
            Icon(
              imageVector = Icons.Filled.Celebration,
              contentDescription = "Closet"
            )
          },
          label = { Text("Closet ($yolksEarned)") },
          modifier = Modifier.testTag("nav_tab_closet")
        )
      }
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showAddTaskSheet = true },
        containerColor = YolkYellowPrimary,
        contentColor = YolkWhite,
        modifier = Modifier.testTag("fab_add_task")
      ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
      }
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      when (selectedTab) {
        MainTab.COMPANION -> CompanionHomeScreen(
          viewModel = viewModel,
          tasks = tasks,
          currentTime = currentTime,
          isSpeaking = isSpeaking,
          isMuted = isMuted,
          spokenText = spokenText,
          yolkMood = yolkMood,
          equippedAccessory = equippedAccessory,
          use3DAvatar = use3DAvatar,
          onToggle3D = { use3DAvatar = !use3DAvatar },
          onAddTaskClick = { showAddTaskSheet = true }
        )

        MainTab.TASKS -> TaskListScreen(
          tasks = tasks,
          currentTime = currentTime,
          onCompleteToggle = { task, isCompleted ->
            if (isCompleted) viewModel.completeTask(task) else viewModel.uncompleteTask(task)
          },
          onDelete = { viewModel.deleteTask(it) },
          onAddTaskClick = { showAddTaskSheet = true }
        )

        MainTab.CLOSET -> EggClosetScreen(
          yolksEarned = yolksEarned,
          equippedAccessory = equippedAccessory,
          unlockedAccessories = unlockedAccessories,
          onEquipAccessory = { viewModel.equipAccessory(it) },
          tasksCompletedCount = tasks.count { it.isCompleted }
        )
      }

      // High-priority 5-Minute Alert Popup Modal
      currentAlertTask?.let { alertTask ->
        YolkAlertDialog(
          task = alertTask,
          spokenText = YolkVoiceLines.get5MinuteAlert(alertTask.title),
          onDismiss = { viewModel.dismissAlert() },
          onSnooze = { viewModel.snoozeAlert(2) },
          onComplete = {
            viewModel.completeTask(alertTask)
            viewModel.dismissAlert()
          },
          onReplayVoice = {
            viewModel.speechEngine.speak(YolkVoiceLines.get5MinuteAlert(alertTask.title))
          }
        )
      }

      // Add Task Bottom Sheet
      if (showAddTaskSheet) {
        AddTaskBottomSheet(
          onDismiss = { showAddTaskSheet = false },
          onAddTask = { title, desc, cat, prio, due ->
            viewModel.addTask(title, desc, cat, prio, due)
          }
        )
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CompanionHomeScreen(
  viewModel: YolkViewModel,
  tasks: List<TaskItem>,
  currentTime: Long,
  isSpeaking: Boolean,
  isMuted: Boolean,
  spokenText: String,
  yolkMood: com.example.viewmodel.YolkMood,
  equippedAccessory: com.example.viewmodel.EggAccessory,
  use3DAvatar: Boolean,
  onToggle3D: () -> Unit,
  onAddTaskClick: () -> Unit
) {
  val upcomingTasks = tasks
    .filter { !it.isCompleted }
    .sortedBy { it.dueTimestamp }
  val nextUpTask = upcomingTasks.firstOrNull()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(6.dp))

      // Avatar Mode Switcher Pill & Companion Status
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = yolkMood.emoji, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Mood: ${yolkMood.label}",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = YolkOrange
            )
          }
        }

        Surface(
          onClick = onToggle3D,
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier.testTag("toggle_avatar_mode")
        ) {
          Text(
            text = if (use3DAvatar) "🎨 Switch to 2D" else "✨ Switch to 3D",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          )
        }
      }
    }

    // Yolk Animated Mascot Character
    item {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        YolkCharacter(
          sizeDp = 190.dp,
          isSpeaking = isSpeaking,
          mood = yolkMood,
          accessory = equippedAccessory,
          use3DPhoto = use3DAvatar,
          onPoke = { viewModel.pokeYolk() }
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Tap Yolk to poke or chat!",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // Live Spoken Speech Bubble
    item {
      YolkSpeechBubble(
        text = spokenText,
        isSpeaking = isSpeaking,
        isMuted = isMuted,
        onReplay = { viewModel.speechEngine.speak(spokenText) },
        onToggleMute = { viewModel.speechEngine.toggleMute() }
      )
    }

    // Quick Test Alert & Presets Section
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = YolkCoral,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "5-Minute Alert Station",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = YolkCoral.copy(alpha = 0.12f)
            ) {
              Text(
                text = "Live Radar",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = YolkCoral,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Yolk alerts you with voice and sound exactly 5 minutes before every task.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Trigger Test 5-Min Alert button
          Button(
            onClick = { viewModel.triggerTest5MinuteAlert() },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("test_5min_alert_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = YolkCoral)
          ) {
            Icon(
              imageVector = Icons.Default.NotificationsActive,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Test 5-Min Alert Now (Spoken Demo)",
              fontWeight = FontWeight.Bold,
              color = Color.White,
              fontSize = 13.sp
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Quick Preset Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            QuickScheduleChip(label = "+5m Quick Task", onClick = {
              viewModel.addTask("Water & Stretch Break", "Hydrate and stretch!", TaskCategory.HEALTH, dueTimestamp = System.currentTimeMillis() + 5 * 60 * 1000)
            }, modifier = Modifier.weight(1f))
            QuickScheduleChip(label = "+15m Meeting", onClick = {
              viewModel.addTask("Important Check-in", "Sync with team", TaskCategory.WORK, dueTimestamp = System.currentTimeMillis() + 15 * 60 * 1000)
            }, modifier = Modifier.weight(1f))
            QuickScheduleChip(label = "+30m Focus", onClick = {
              viewModel.addTask("Deep Work Sprint", "Zero distractions!", TaskCategory.STUDY, dueTimestamp = System.currentTimeMillis() + 30 * 60 * 1000)
            }, modifier = Modifier.weight(1f))
          }
        }
      }
    }

    // Next Upcoming Task Card
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Next Scheduled Task",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        if (nextUpTask != null) {
          Text(
            text = "Total Active: ${upcomingTasks.size}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      if (nextUpTask != null) {
        YolkTaskCard(
          task = nextUpTask,
          currentTimeMillis = currentTime,
          onCompleteToggle = { isComp ->
            if (isComp) viewModel.completeTask(nextUpTask) else viewModel.uncompleteTask(nextUpTask)
          },
          onDelete = { viewModel.deleteTask(nextUpTask) }
        )
      } else {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(text = "🐣", fontSize = 32.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "No upcoming tasks right now!",
              fontWeight = FontWeight.Bold,
              style = MaterialTheme.typography.titleSmall
            )
            Text(
              text = "Tap below to hatch a task or test the 5-min alert!",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
              onClick = onAddTaskClick,
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = YolkYellowPrimary)
            ) {
              Text("Add a Task", fontWeight = FontWeight.Bold, color = YolkWhite)
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}

@Composable
private fun QuickScheduleChip(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Surface(
    onClick = onClick,
    shape = RoundedCornerShape(10.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
    modifier = modifier
  ) {
    Box(
      modifier = Modifier.padding(vertical = 8.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = label,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface
      )
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TaskListScreen(
  tasks: List<TaskItem>,
  currentTime: Long,
  onCompleteToggle: (TaskItem, Boolean) -> Unit,
  onDelete: (TaskItem) -> Unit,
  onAddTaskClick: () -> Unit
) {
  var filterCategory by remember { mutableStateOf<TaskCategory?>(null) }
  var showCompletedOnly by remember { mutableStateOf(false) }

  val filteredTasks = tasks.filter { task ->
    val matchesCategory = filterCategory == null || task.category == filterCategory
    val matchesCompletion = if (showCompletedOnly) task.isCompleted else true
    matchesCategory && matchesCompletion
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(6.dp))
      // Filter Chips
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        FilterChip(
          selected = filterCategory == null && !showCompletedOnly,
          onClick = {
            filterCategory = null
            showCompletedOnly = false
          },
          label = { Text("All (${tasks.size})", fontSize = 12.sp) }
        )
        FilterChip(
          selected = showCompletedOnly,
          onClick = { showCompletedOnly = !showCompletedOnly },
          label = { Text("Completed (${tasks.count { it.isCompleted }})", fontSize = 12.sp) }
        )
        TaskCategory.values().forEach { cat ->
          val count = tasks.count { it.category == cat }
          FilterChip(
            selected = filterCategory == cat,
            onClick = { filterCategory = if (filterCategory == cat) null else cat },
            label = { Text("${cat.emoji} ${cat.displayName} ($count)", fontSize = 12.sp) }
          )
        }
      }
    }

    if (filteredTasks.isEmpty()) {
      item {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp)
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(text = "🥚", fontSize = 36.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "No tasks found here!",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Schedule tasks to get 5-minute spoken warnings from Yolk!",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
              onClick = onAddTaskClick,
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = YolkYellowPrimary)
            ) {
              Text("Schedule a Task", color = YolkWhite, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    } else {
      items(filteredTasks, key = { it.id }) { task ->
        YolkTaskCard(
          task = task,
          currentTimeMillis = currentTime,
          onCompleteToggle = { onCompleteToggle(task, it) },
          onDelete = { onDelete(task) }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}
