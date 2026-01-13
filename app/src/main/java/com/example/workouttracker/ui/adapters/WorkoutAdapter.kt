package com.example.workouttracker.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.workouttracker.R
import com.example.workouttracker.data.local.entity.Workout
import com.example.workouttracker.databinding.ItemWorkoutBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WorkoutAdapter(
    private val onWorkoutClick: (Workout) -> Unit
) : ListAdapter<Workout, WorkoutAdapter.WorkoutViewHolder>(WorkoutDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val binding = ItemWorkoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WorkoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WorkoutViewHolder(
        private val binding: ItemWorkoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
        private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onWorkoutClick(getItem(position))
                }
            }
        }

        fun bind(workout: Workout) {
            val date = Date(workout.date)

            binding.workoutName.text = workout.name
            binding.workoutDate.text = dateFormat.format(date)
            binding.workoutTime.text = timeFormat.format(date).lowercase()

            if (workout.isCompleted) {
                binding.workoutStatus.text = "Completed"
                binding.workoutStatus.setBackgroundResource(R.drawable.chip_background_completed)
            } else {
                binding.workoutStatus.text = "In Progress"
                binding.workoutStatus.setBackgroundResource(R.drawable.chip_background)
            }

            binding.exerciseCount.text = "0 exercises"
        }
    }

    private class WorkoutDiffCallback : DiffUtil.ItemCallback<Workout>() {
        override fun areItemsTheSame(oldItem: Workout, newItem: Workout): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Workout, newItem: Workout): Boolean {
            return oldItem == newItem
        }
    }
}
