package com.example.workouttracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.workouttracker.data.ExerciseSuggestions
import com.example.workouttracker.data.local.dao.ExerciseDao
import com.example.workouttracker.data.local.dao.WorkoutDao
import com.example.workouttracker.data.local.entity.Exercise
import com.example.workouttracker.data.local.entity.Workout
import com.example.workouttracker.data.local.entity.WorkoutSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.exerciseDao())
                    }
                }
            }

            suspend fun populateDatabase(exerciseDao: ExerciseDao) {
                val defaultExercises = ExerciseSuggestions.getDefaultExercises()
                defaultExercises.forEach { exercise ->
                    exerciseDao.insertExercise(exercise)
                }
            }
        }
    }
}
