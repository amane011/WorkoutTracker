package com.example.workouttracker.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.workouttracker.data.local.entity.WorkoutSet
import com.example.workouttracker.databinding.ItemSetBinding

class SetAdapter(
    private val onDeleteClick: (WorkoutSet) -> Unit
) : ListAdapter<WorkoutSet, SetAdapter.SetViewHolder>(SetDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetViewHolder {
        val binding = ItemSetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SetViewHolder(
        private val binding: ItemSetBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnDelete.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClick(getItem(position))
                }
            }
        }

        fun bind(set: WorkoutSet) {
            binding.setNumber.text = set.setNumber.toString()
            binding.reps.text = "${set.reps} reps"
            binding.weight.text = set.weight?.let { "${it.toInt()} kg" } ?: "—"
        }
    }

    private class SetDiffCallback : DiffUtil.ItemCallback<WorkoutSet>() {
        override fun areItemsTheSame(oldItem: WorkoutSet, newItem: WorkoutSet): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WorkoutSet, newItem: WorkoutSet): Boolean {
            return oldItem == newItem
        }
    }
}
