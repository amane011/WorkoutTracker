package com.example.workouttracker.ui.screens.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.data.local.entity.Workout
import com.example.workouttracker.data.repository.WorkoutRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: WorkoutRepository
) : ViewModel() {

    val workouts: LiveData<List<Workout>> = repository.allWorkouts.asLiveData()

    val completedWorkoutCount: LiveData<Int> = repository.completedWorkoutCount.asLiveData()

    fun createNewWorkout(name: String, onWorkoutCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val workout = Workout(name = name)
            val workoutId = repository.insertWorkout(workout)
            onWorkoutCreated(workoutId)
        }
    }

    fun deleteWorkout(workout: Workout) {
        viewModelScope.launch {
            repository.deleteWorkout(workout)
        }
    }

    companion object {
        fun factory(repository: WorkoutRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(repository) as T
                }
            }
        }
    }
}
