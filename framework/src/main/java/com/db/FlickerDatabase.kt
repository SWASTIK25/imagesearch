package com.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [FlickerEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FlickerDatabase : RoomDatabase() {

  companion object {
    // Singleton prevents multiple instances of database opening at the
    // same time.
    @Volatile
    private var INSTANCE: FlickerDatabase? = null

    fun getDatabase(context: Context): FlickerDatabase {
      val tempInstance = INSTANCE
      if (tempInstance != null) {
        return tempInstance
      }
      synchronized(this) {
        val instance = Room.databaseBuilder(
                context.applicationContext,
                FlickerDatabase::class.java,
                "word_database"
        ).build()
        INSTANCE = instance
        return instance
      }
    }
  }

  abstract fun flickerDao(): FlickerDao
}