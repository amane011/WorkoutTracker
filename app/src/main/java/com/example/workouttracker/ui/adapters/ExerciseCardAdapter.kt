package com.example.workouttracker.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.workouttracker.data.local.entity.Exercise
import com.example.workouttracker.data.local.entity.WorkoutSet
import com.example.workouttracker.databinding.ItemExerciseCardBinding

data class ExerciseWithSets(
    val exercise: Exercise,
    val sets: List<WorkoutSet>
)

class ExerciseCardAdapter(
    private val onAddSetClick: (Exercise) -> Unit,
    private val onDeleteSetClick: (WorkoutSet) -> Unit,
    private val onRemoveExerciseClick: (Exercise) -> Unit
) : ListAdapter<ExerciseWithSets, ExerciseCardAdapter.ExerciseCardViewHolder>(ExerciseWithSetsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseCardViewHolder {
        val binding = ItemExerciseCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExerciseCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseCardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ExerciseCardViewHolder(
        private val binding: ItemExerciseCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val setAdapter = SetAdapter { set ->
            onDeleteSetClick(set)
        }

        init {
            binding.setsRecycler.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = setAdapter
            }

            binding.btnAddSet.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onAddSetClick(getItem(position).exercise)
                }
            }

            binding.btnRemoveExercise.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onRemoveExerciseClick(getItem(position).exercise)
                }
            }
        }

        fun bind(item: ExerciseWithSets) {
            binding.exerciseName.text = item.exercise.name

            item.exercise.muscleGroup?.let { muscle ->
                binding.muscleGroup.text = muscle.uppercase()
                binding.muscleGroup.isVisible = true
            } ?: run {
                binding.muscleGroup.isVisible = false
            }

            val sortedSets = item.sets.sortedBy { it.setNumber }
            setAdapter.submitList(sortedSets)

            binding.emptySets.isVisible = item.sets.isEmpty()
            binding.setsRecycler.isVisible = item.sets.isNotEmpty()
        }
    }

    private class ExerciseWithSetsDiffCallback : DiffUtil.ItemCallback<ExerciseWithSets>() {
        override fun areItemsTheSame(oldItem: ExerciseWithSets, newItem: ExerciseWithSets): Boolean {
            return oldItem.exercise.id == newItem.exercise.id
        }

        override fun areContentsTheSame(oldItem: ExerciseWithSets, newItem: ExerciseWithSets): Boolean {
            return oldItem == newItem
        }
    }
}
