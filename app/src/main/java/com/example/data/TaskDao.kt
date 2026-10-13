package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
  @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueTimestamp ASC")
  fun getAllTasks(): Flow<List<TaskEntity>>

  @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
  suspend fun getTaskById(taskId: Long): TaskEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTask(task: TaskEntity): Long

  @Update
  suspend fun updateTask(task: TaskEntity)

  @Delete
  suspend fun deleteTask(task: TaskEntity)

  @Query("DELETE FROM tasks WHERE id = :taskId")
  suspend fun deleteTaskById(taskId: Long)

  @Query("UPDATE tasks SET alert5MinTriggered = 1 WHERE id = :taskId")
  suspend fun markAlertTriggered(taskId: Long)

  @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :taskId")
  suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean)
}
