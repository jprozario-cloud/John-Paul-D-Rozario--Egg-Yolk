package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.TaskCategory
import com.example.data.TaskItem
import com.example.data.TaskPriority
import com.example.data.TaskRepository
import com.example.data.YolkDatabase
import com.example.reminder.YolkNotificationHelper
import com.example.speech.YolkSpeechEngine
import com.example.speech.YolkVoiceLines
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class YolkMood(val label: String, val emoji: String) {
  SUNNY("Sunny-Side Up", "🍳"),
  ALERT("Alert Active!", "🚨"),
  FOCUSED("Boiled & Focused", "🥚"),
  CELEBRATING("Golden Victorious", "🌟")
}

enum class EggAccessory(val id: String, val title: String, val emoji: String, val cost: Int) {
  NONE("none", "Natural Shell", "🥚", 0),
  CROWN("crown", "Golden Crown", "👑", 20),
  SHADES("shades", "Cool Shades", "🕶️", 15),
  CHEF("chef", "Master Chef Toque", "👨‍🍳", 25),
  PARTY("party", "Party Cone Hat", "🎉", 10)
}

class YolkViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: TaskRepository
  val speechEngine: YolkSpeechEngine
  private val notificationHelper: YolkNotificationHelper

  val allTasks: StateFlow<List<TaskItem>>

  private val _currentTimeMillis = MutableStateFlow(System.currentTimeMillis())
  val currentTimeMillis: StateFlow<Long> = _currentTimeMillis.asStateFlow()

  private val _currentAlertTask = MutableStateFlow<TaskItem?>(null)
  val currentAlertTask: StateFlow<TaskItem?> = _currentAlertTask.asStateFlow()

  private val _yolkMood = MutableStateFlow(YolkMood.SUNNY)
  val yolkMood: StateFlow<YolkMood> = _yolkMood.asStateFlow()

  private val _equippedAccessory = MutableStateFlow(EggAccessory.NONE)
  val equippedAccessory: StateFlow<EggAccessory> = _equippedAccessory.asStateFlow()

  private val _yolksEarned = MutableStateFlow(30) // Starter gift
  val yolksEarned: StateFlow<Int> = _yolksEarned.asStateFlow()

  private val _unlockedAccessories = MutableStateFlow(setOf(EggAccessory.NONE.id, EggAccessory.PARTY.id))
  val unlockedAccessories: StateFlow<Set<String>> = _unlockedAccessories.asStateFlow()

  private val _pokeCount = MutableStateFlow(0)
  val pokeCount: StateFlow<Int> = _pokeCount.asStateFlow()

  init {
    val db = YolkDatabase.getDatabase(application)
    repository = TaskRepository(db.taskDao())
    speechEngine = YolkSpeechEngine(application)
    notificationHelper = YolkNotificationHelper(application)

    allTasks = repository.allTasks.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

    // Initial greeting after brief pause
    viewModelScope.launch {
      delay(700)
      val greeting = YolkVoiceLines.getGreeting()
      speechEngine.speak(greeting)
    }

    // Ticker loop: update current time every second and check 5-minute alerts
    viewModelScope.launch {
      while (isActive) {
        val now = System.currentTimeMillis()
        _currentTimeMillis.value = now
        checkUpcoming5MinAlerts(now)
        delay(1000)
      }
    }
  }

  private suspend fun checkUpcoming5MinAlerts(now: Long) {
    val tasks = allTasks.value
    // Threshold: task is upcoming within 5 minutes (<= 300,000 ms and >= -60,000 ms) and not completed and not yet alerted
    val candidate = tasks.firstOrNull { task ->
      !task.isCompleted &&
          !task.alert5MinTriggered &&
          (task.dueTimestamp - now) in 0..(5 * 60 * 1000)
    }

    if (candidate != null && _currentAlertTask.value?.id != candidate.id) {
      trigger5MinuteAlertForTask(candidate)
    }
  }

  private suspend fun trigger5MinuteAlertForTask(task: TaskItem) {
    _currentAlertTask.value = task
    _yolkMood.value = YolkMood.ALERT
    repository.markAlertTriggered(task.id)

    val spokenText = YolkVoiceLines.get5MinuteAlert(task.title)
    speechEngine.speak(spokenText)
    notificationHelper.show5MinuteAlertNotification(task.id, task.title, spokenText)
  }

  fun pokeYolk() {
    _pokeCount.value += 1
    notificationHelper.vibratePoke()
    val line = YolkVoiceLines.getPokeReaction()
    speechEngine.speak(line)
  }

  fun addTask(
    title: String,
    description: String = "",
    category: TaskCategory = TaskCategory.WORK,
    priority: TaskPriority = TaskPriority.MEDIUM,
    dueTimestamp: Long
  ) {
    viewModelScope.launch {
      val newTask = TaskItem(
        title = title.trim(),
        description = description.trim(),
        category = category,
        priority = priority,
        dueTimestamp = dueTimestamp,
        isCompleted = false,
        alert5MinTriggered = false
      )
      repository.insertTask(newTask)

      // Friendly confirmation
      val response = "Egg-cellent! I've scheduled '$title' and I'll alert you 5 minutes before!"
      speechEngine.speak(response)
    }
  }

  fun triggerTest5MinuteAlert() {
    viewModelScope.launch {
      val now = System.currentTimeMillis()
      // Create task due exactly 5 minutes from now
      val testTask = TaskItem(
        title = "Team Quick Sync",
        description = "5-minute preview alert demo with Yolk!",
        category = TaskCategory.WORK,
        priority = TaskPriority.HIGH,
        dueTimestamp = now + (5 * 60 * 1000),
        isCompleted = false,
        alert5MinTriggered = false
      )
      val newId = repository.insertTask(testTask)
      val createdTask = testTask.copy(id = newId)
      trigger5MinuteAlertForTask(createdTask)
    }
  }

  fun completeTask(task: TaskItem) {
    viewModelScope.launch {
      repository.updateTaskCompletion(task.id, true)
      _yolksEarned.value += 10
      _yolkMood.value = YolkMood.CELEBRATING
      notificationHelper.vibratePoke()

      val praise = YolkVoiceLines.getTaskCompleted()
      speechEngine.speak(praise)

      // Reset celebration mood back to sunny after 4 seconds
      delay(4000)
      if (_yolkMood.value == YolkMood.CELEBRATING) {
        _yolkMood.value = YolkMood.SUNNY
      }
    }
  }

  fun uncompleteTask(task: TaskItem) {
    viewModelScope.launch {
      repository.updateTaskCompletion(task.id, false)
    }
  }

  fun deleteTask(task: TaskItem) {
    viewModelScope.launch {
      if (_currentAlertTask.value?.id == task.id) {
        _currentAlertTask.value = null
      }
      repository.deleteTask(task)
    }
  }

  fun dismissAlert() {
    _currentAlertTask.value = null
    _yolkMood.value = YolkMood.SUNNY
    speechEngine.stop()
  }

  fun snoozeAlert(minutes: Int = 2) {
    val task = _currentAlertTask.value ?: return
    viewModelScope.launch {
      val newDue = System.currentTimeMillis() + (minutes * 60 * 1000) + (5 * 60 * 1000)
      val updated = task.copy(
        dueTimestamp = newDue,
        alert5MinTriggered = false
      )
      repository.updateTask(updated)
      _currentAlertTask.value = null
      _yolkMood.value = YolkMood.SUNNY

      val snoozeLine = "Got it! Snoozing for $minutes minutes! I'll remind you soon!"
      speechEngine.speak(snoozeLine)
    }
  }

  fun equipAccessory(accessory: EggAccessory) {
    if (_unlockedAccessories.value.contains(accessory.id)) {
      _equippedAccessory.value = accessory
      speechEngine.speak("Ooh, look at me! Sporting the ${accessory.title} like an egg-star!")
    } else if (_yolksEarned.value >= accessory.cost) {
      _yolksEarned.value -= accessory.cost
      _unlockedAccessories.value = _unlockedAccessories.value + accessory.id
      _equippedAccessory.value = accessory
      speechEngine.speak("Egg-ceptional style unlocked! The ${accessory.title} looks stunning!")
    } else {
      speechEngine.speak("Keep completing tasks to hatch more Golden Yolks for that!")
    }
  }

  override fun onCleared() {
    super.onCleared()
    speechEngine.shutdown()
  }
}
