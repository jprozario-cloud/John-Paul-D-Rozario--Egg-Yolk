package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(private val taskDao: TaskDao) {
  val allTasks: Flow<List<TaskItem>> = taskDao.getAllTasks().map { entities ->
    entities.map { it.toTaskItem() }
  }

  suspend fun insertTask(task: TaskItem): Long {
    return taskDao.insertTask(TaskEntity.fromTaskItem(task))
  }

  suspend fun updateTask(task: TaskItem) {
    taskDao.updateTask(TaskEntity.fromTaskItem(task))
  }

  suspend fun deleteTask(task: TaskItem) {
    taskDao.deleteTask(TaskEntity.fromTaskItem(task))
  }

  suspend fun deleteTaskById(taskId: Long) {
    taskDao.deleteTaskById(taskId)
  }

  suspend fun markAlertTriggered(taskId: Long) {
    taskDao.markAlertTriggered(taskId)
  }

  suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean) {
    taskDao.updateTaskCompletion(taskId, isCompleted)
  }
}
