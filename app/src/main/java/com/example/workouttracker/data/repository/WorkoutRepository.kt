package com.example.workouttracker.data.repository

import com.example.workouttracker.data.local.dao.ExerciseDao
import com.example.workouttracker.data.local.dao.WorkoutDao
import com.example.workouttracker.data.local.entity.Exercise
import com.example.workouttracker.data.local.entity.Workout
import com.example.workouttracker.data.local.entity.WorkoutSet
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(
    private val exerciseDao: ExerciseDao,
    private val workoutDao: WorkoutDao
) {
    // Exercise operations
    val allExercises: Flow<List<Exercise>> = exerciseDao.getAllExercises()

    suspend fun getExerciseById(id: Long): Exercise? = exerciseDao.getExerciseById(id)

    suspend fun insertExercise(exercise: Exercise): Long = exerciseDao.insertExercise(exercise)

    suspend fun updateExercise(exercise: Exercise) = exerciseDao.updateExercise(exercise)

    suspend fun deleteExercise(exercise: Exercise) = exerciseDao.deleteExercise(exercise)

    fun getExercisesByMuscleGroup(muscleGroup: String): Flow<List<Exercise>> =
        exerciseDao.getExercisesByMuscleGroup(muscleGroup)

    // Workout operations
    val allWorkouts: Flow<List<Workout>> = workoutDao.getAllWorkouts()

    val completedWorkoutCount: Flow<Int> = workoutDao.getCompletedWorkoutCount()

    suspend fun getWorkoutById(id: Long): Workout? = workoutDao.getWorkoutById(id)

    suspend fun getActiveWorkout(): Workout? = workoutDao.getActiveWorkout()

    suspend fun insertWorkout(workout: Workout): Long = workoutDao.insertWorkout(workout)

    suspend fun updateWorkout(workout: Workout) = workoutDao.updateWorkout(workout)

    suspend fun deleteWorkout(workout: Workout) = workoutDao.deleteWorkout(workout)

    // WorkoutSet operations
    fun getSetsForWorkout(workoutId: Long): Flow<List<WorkoutSet>> =
        workoutDao.getSetsForWorkout(workoutId)

    suspend fun insertSet(workoutSet: WorkoutSet): Long = workoutDao.insertSet(workoutSet)

    suspend fun updateSet(workoutSet: WorkoutSet) = workoutDao.updateSet(workoutSet)

    suspend fun deleteSet(workoutSet: WorkoutSet) = workoutDao.deleteSet(workoutSet)

    suspend fun deleteSetsForExercise(workoutId: Long, exerciseId: Long) =
        workoutDao.deleteSetsForExercise(workoutId, exerciseId)
}
