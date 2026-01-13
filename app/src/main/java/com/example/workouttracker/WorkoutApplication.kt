package com.example.workouttracker

import android.app.Application
import com.example.workouttracker.di.AppContainer

class WorkoutApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
