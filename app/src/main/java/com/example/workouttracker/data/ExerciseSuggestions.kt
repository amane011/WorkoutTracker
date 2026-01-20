package com.example.workouttracker.data

import com.example.workouttracker.data.local.entity.Exercise

object ExerciseSuggestions {

    private val defaultExercises = mapOf(
        "Chest" to listOf("Bench Press", "Incline Dumbbell Press", "Push-ups", "Cable Fly", "Dips"),
        "Back" to listOf("Pull-ups", "Barbell Row", "Lat Pulldown", "Seated Cable Row", "Deadlift"),
        "Shoulders" to listOf("Overhead Press", "Lateral Raise", "Face Pull", "Arnold Press", "Rear Delt Fly"),
        "Arms" to listOf("Bicep Curl", "Tricep Pushdown", "Hammer Curl", "Skull Crushers", "Preacher Curl"),
        "Legs" to listOf("Squat", "Leg Press", "Romanian Deadlift", "Leg Curl", "Calf Raise", "Lunges"),
        "Core" to listOf("Plank", "Crunches", "Russian Twist", "Leg Raise", "Ab Wheel Rollout")
    )

    fun getDefaultExercises(): List<Exercise> {
        return defaultExercises.flatMap { (muscleGroup, exercises) ->
            exercises.map { name ->
                Exercise(name = name, muscleGroup = muscleGroup)
            }
        }
    }
}
