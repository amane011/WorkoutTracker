package com.example.workouttracker.ui.screens.exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.data.local.entity.Exercise
import com.example.workouttracker.data.repository.WorkoutRepository
import kotlinx.coroutines.launch

class ExerciseViewModel(
    private val repository: WorkoutRepository
) : ViewModel() {

    private val _exerciseSaved = MutableLiveData<Boolean>()
    val exerciseSaved: LiveData<Boolean> = _exerciseSaved

    val exercises: LiveData<List<Exercise>> = repository.allExercises.asLiveData()

    fun createExercise(name: String, muscleGroup: String?) {
        viewModelScope.launch {
            val exercise = Exercise(
                name = name,
                muscleGroup = muscleGroup?.takeIf { it.isNotBlank() }
            )
            repository.insertExercise(exercise)
            _exerciseSaved.value = true
        }
    }

    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch {
            repository.deleteExercise(exercise)
        }
    }

    fun resetSavedState() {
        _exerciseSaved.value = false
    }

    companion object {
        fun factory(repository: WorkoutRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ExerciseViewModel(repository) as T
                }
            }
        }
    }
}
