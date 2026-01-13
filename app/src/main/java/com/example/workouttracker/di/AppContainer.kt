package com.example.workouttracker.di

import android.content.Context
import com.example.workouttracker.data.local.WorkoutDatabase
import com.example.workouttracker.data.repository.WorkoutRepository

class AppContainer(context: Context) {

    private val database = WorkoutDatabase.getDatabase(context)

    val workoutRepository: WorkoutRepository by lazy {
        WorkoutRepository(
            exerciseDao = database.exerciseDao(),
            workoutDao = database.workoutDao()
        )
    }
}
