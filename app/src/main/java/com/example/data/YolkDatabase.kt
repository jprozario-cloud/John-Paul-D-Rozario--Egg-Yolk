package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class YolkDatabase : RoomDatabase() {
  abstract fun taskDao(): TaskDao

  companion object {
    @Volatile
    private var INSTANCE: YolkDatabase? = null

    fun getDatabase(context: Context): YolkDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          YolkDatabase::class.java,
          "yolk_companion_db"
        )
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
