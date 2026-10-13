package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val title: String,
  val description: String = "",
  val categoryName: String = TaskCategory.WORK.name,
  val priorityName: String = TaskPriority.MEDIUM.name,
  val dueTimestamp: Long,
  val isCompleted: Boolean = false,
  val alert5MinTriggered: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
) {
  fun toTaskItem(): TaskItem {
    val category = runCatching { TaskCategory.valueOf(categoryName) }.getOrDefault(TaskCategory.WORK)
    val priority = runCatching { TaskPriority.valueOf(priorityName) }.getOrDefault(TaskPriority.MEDIUM)
    return TaskItem(
      id = id,
      title = title,
      description = description,
      category = category,
      priority = priority,
      dueTimestamp = dueTimestamp,
      isCompleted = isCompleted,
      alert5MinTriggered = alert5MinTriggered,
      createdAt = createdAt
    )
  }

  companion object {
    fun fromTaskItem(item: TaskItem): TaskEntity {
      return TaskEntity(
        id = item.id,
        title = item.title,
        description = item.description,
        categoryName = item.category.name,
        priorityName = item.priority.name,
        dueTimestamp = item.dueTimestamp,
        isCompleted = item.isCompleted,
        alert5MinTriggered = item.alert5MinTriggered,
        createdAt = item.createdAt
      )
    }
  }
}
