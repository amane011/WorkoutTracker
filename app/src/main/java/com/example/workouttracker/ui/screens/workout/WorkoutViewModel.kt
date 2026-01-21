package com.example.workouttracker.ui.screens.workout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.workouttracker.data.local.entity.Exercise
import com.example.workouttracker.data.local.entity.Workout
import com.example.workouttracker.data.local.entity.WorkoutSet
import com.example.workouttracker.data.repository.WorkoutRepository
import kotlinx.coroutines.launch

class WorkoutViewModel(
    private val repository: WorkoutRepository,
    private val workoutId: Long
) : ViewModel() {

    private val _workout = MutableLiveData<Workout?>()
    val workout: LiveData<Workout?> = _workout

    val exercises: LiveData<List<Exercise>> = repository.allExercises.asLiveData()

    val workoutSets: LiveData<List<WorkoutSet>> = repository.getSetsForWorkout(workoutId).asLiveData()

    init {
        loadWorkout()
    }

    private fun loadWorkout() {
        viewModelScope.launch {
            _workout.value = repository.getWorkoutById(workoutId)
        }
    }

    fun addSet(exerciseId: Long, reps: Int, weight: Double?) {
        viewModelScope.launch {
            val currentSets = workoutSets.value?.filter { it.exerciseId == exerciseId } ?: emptyList()
            val nextSetNumber = (currentSets.maxOfOrNull { it.setNumber } ?: 0) + 1

            val newSet = WorkoutSet(
                workoutId = workoutId,
                exerciseId = exerciseId,
                setNumber = nextSetNumber,
                reps = reps,
                weight = weight
            )
            repository.insertSet(newSet)
        }
    }

    fun deleteSet(workoutSet: WorkoutSet) {
        viewModelScope.launch {
            repository.deleteSet(workoutSet)
        }
    }

    fun removeExerciseFromWorkout(exerciseId: Long) {
        viewModelScope.launch {
            repository.deleteSetsForExercise(workoutId, exerciseId)
        }
    }

    fun completeWorkout() {
        viewModelScope.launch {
            _workout.value?.let { workout ->
                repository.updateWorkout(workout.copy(isCompleted = true))
                loadWorkout()
            }
        }
    }

    fun createExercise(name: String, muscleGroup: String, onCreated: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val exercise = Exercise(name = name, muscleGroup = muscleGroup)
            val exerciseId = repository.insertExercise(exercise)
            onCreated(exerciseId)
        }
    }

    companion object {
        fun factory(repository: WorkoutRepository, workoutId: Long): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return WorkoutViewModel(repository, workoutId) as T
                }
            }
        }
    }
}
