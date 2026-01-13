package com.example.workouttracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.workouttracker.data.local.dao.ExerciseDao
import com.example.workouttracker.data.local.dao.WorkoutDao
import com.example.workouttracker.data.local.entity.Exercise
import com.example.workouttracker.data.local.entity.Workout
import com.example.workouttracker.data.local.entity.WorkoutSet

@Database(
    entities = [Exercise::class, Workout::class, WorkoutSet::class],
    version = 1,
    exportSchema = false
)
abstract class WorkoutDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: WorkoutDatabase? = null

        fun getDatabase(context: Context): WorkoutDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkoutDatabase::class.java,
                    "workout_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
