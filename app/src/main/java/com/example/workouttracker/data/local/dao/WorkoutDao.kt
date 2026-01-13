package com.example.workouttracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.workouttracker.data.local.entity.Workout
import com.example.workouttracker.data.local.entity.WorkoutSet
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Query("SELECT * FROM workouts ORDER BY date DESC")
    fun getAllWorkouts(): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutById(id: Long): Workout?

    @Query("SELECT * FROM workouts WHERE isCompleted = 0 ORDER BY date DESC LIMIT 1")
    suspend fun getActiveWorkout(): Workout?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: Workout): Long

    @Update
    suspend fun updateWorkout(workout: Workout)

    @Delete
    suspend fun deleteWorkout(workout: Workout)

    @Query("SELECT * FROM workout_sets WHERE workoutId = :workoutId ORDER BY exerciseId, setNumber")
    fun getSetsForWorkout(workoutId: Long): Flow<List<WorkoutSet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(workoutSet: WorkoutSet): Long

    @Update
    suspend fun updateSet(workoutSet: WorkoutSet)

    @Delete
    suspend fun deleteSet(workoutSet: WorkoutSet)

    @Query("DELETE FROM workout_sets WHERE workoutId = :workoutId AND exerciseId = :exerciseId")
    suspend fun deleteSetsForExercise(workoutId: Long, exerciseId: Long)

    @Query("SELECT COUNT(*) FROM workouts WHERE isCompleted = 1")
    fun getCompletedWorkoutCount(): Flow<Int>
}
