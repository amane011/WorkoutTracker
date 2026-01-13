package com.example.workouttracker.ui.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workouttracker.R
import com.example.workouttracker.WorkoutApplication
import com.example.workouttracker.databinding.FragmentHomeBinding
import com.example.workouttracker.databinding.DialogNewWorkoutBinding
import com.example.workouttracker.ui.adapters.WorkoutAdapter
import com.example.workouttracker.ui.screens.exercise.ExercisesFragment
import com.example.workouttracker.ui.screens.workout.WorkoutFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        val app = requireActivity().application as WorkoutApplication
        HomeViewModel.factory(app.container.workoutRepository)
    }

    private lateinit var workoutAdapter: WorkoutAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        workoutAdapter = WorkoutAdapter { workout ->
            navigateToWorkout(workout.id)
        }

        binding.workoutsRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = workoutAdapter
        }
    }

    private fun setupClickListeners() {
        binding.fabNewWorkout.setOnClickListener {
            showNewWorkoutDialog()
        }

        binding.btnManageExercises.setOnClickListener {
            navigateToExercises()
        }
    }

    private fun observeViewModel() {
        viewModel.workouts.observe(viewLifecycleOwner) { workouts ->
            workoutAdapter.submitList(workouts)
            binding.emptyState.isVisible = workouts.isEmpty()
            binding.workoutsRecycler.isVisible = workouts.isNotEmpty()
        }

        viewModel.completedWorkoutCount.observe(viewLifecycleOwner) { count ->
            binding.completedCount.text = count.toString()
        }
    }

    private fun showNewWorkoutDialog() {
        val dialogBinding = DialogNewWorkoutBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.Theme_WorkoutTracker)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnStart.setOnClickListener {
            val name = dialogBinding.etWorkoutName.text.toString().ifBlank { "Workout" }
            viewModel.createNewWorkout(name) { workoutId ->
                dialog.dismiss()
                navigateToWorkout(workoutId)
            }
        }

        dialog.show()
    }

    private fun navigateToWorkout(workoutId: Long) {
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(R.id.fragment_container, WorkoutFragment.newInstance(workoutId))
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToExercises() {
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(R.id.fragment_container, ExercisesFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
