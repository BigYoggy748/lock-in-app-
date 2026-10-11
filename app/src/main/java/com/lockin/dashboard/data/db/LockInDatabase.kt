package com.lockin.dashboard.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lockin.dashboard.data.dao.DayLogDao
import com.lockin.dashboard.data.dao.FocusSessionDao
import com.lockin.dashboard.data.dao.NoteDao
import com.lockin.dashboard.data.dao.TaskDao
import com.lockin.dashboard.data.model.DayLog
import com.lockin.dashboard.data.model.FocusSession
import com.lockin.dashboard.data.model.Note
import com.lockin.dashboard.data.model.Task

@Database(
    entities = [DayLog::class, Task::class, FocusSession::class, Note::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LockInDatabase : RoomDatabase() {
    abstract fun dayLogDao(): DayLogDao
    abstract fun taskDao(): TaskDao
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile private var INSTANCE: LockInDatabase? = null

        fun getDatabase(context: Context): LockInDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    LockInDatabase::class.java,
                    "lockin_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
            }
        }
    }
}
