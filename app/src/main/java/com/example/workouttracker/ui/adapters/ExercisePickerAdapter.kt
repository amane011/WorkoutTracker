package com.example.workouttracker.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.workouttracker.R
import com.example.workouttracker.data.local.entity.Exercise
import com.example.workouttracker.databinding.ItemExercisePickerBinding

class ExercisePickerAdapter(
    private val onExerciseClick: (Exercise) -> Unit
) : ListAdapter<Exercise, ExercisePickerAdapter.ExercisePickerViewHolder>(ExerciseDiffCallback()) {

    private var selectedExerciseIds: Set<Long> = emptySet()

    fun setSelectedExerciseIds(ids: Set<Long>) {
        selectedExerciseIds = ids
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExercisePickerViewHolder {
        val binding = ItemExercisePickerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExercisePickerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExercisePickerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ExercisePickerViewHolder(
        private val binding: ItemExercisePickerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val exercise = getItem(position)
                    if (exercise.id !in selectedExerciseIds) {
                        onExerciseClick(exercise)
                    }
                }
            }
        }

        fun bind(exercise: Exercise) {
            val isSelected = exercise.id in selectedExerciseIds
            val context = binding.root.context

            binding.exerciseName.text = exercise.name

            if (isSelected) {
                binding.root.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.primary)
                )
                binding.exerciseName.setTextColor(
                    ContextCompat.getColor(context, R.color.on_primary)
                )
                binding.checkIcon.visibility = android.view.View.VISIBLE
            } else {
                binding.root.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.surface_variant)
                )
                binding.exerciseName.setTextColor(
                    ContextCompat.getColor(context, R.color.on_surface)
                )
                binding.checkIcon.visibility = android.view.View.GONE
            }

            binding.root.isEnabled = !isSelected
            binding.root.alpha = if (isSelected) 0.7f else 1f
        }
    }

    private class ExerciseDiffCallback : DiffUtil.ItemCallback<Exercise>() {
        override fun areItemsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
            return oldItem == newItem
        }
    }
}
