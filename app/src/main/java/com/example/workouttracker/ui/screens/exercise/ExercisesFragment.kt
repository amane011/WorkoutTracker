package com.example.workouttracker.ui.screens.exercise

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
import com.example.workouttracker.databinding.FragmentExercisesBinding
import com.example.workouttracker.ui.adapters.ExerciseAdapter
import com.google.android.material.chip.Chip

class ExercisesFragment : Fragment() {

    private var _binding: FragmentExercisesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExerciseViewModel by viewModels {
        val app = requireActivity().application as WorkoutApplication
        ExerciseViewModel.factory(app.container.workoutRepository)
    }

    private lateinit var exerciseAdapter: ExerciseAdapter
    private var selectedMuscleGroup: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExercisesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        setupChipGroup()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView() {
        exerciseAdapter = ExerciseAdapter { exercise ->
            viewModel.deleteExercise(exercise)
        }

        binding.exercisesRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = exerciseAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnCreateExercise.setOnClickListener {
            createExercise()
        }
    }

    private fun setupChipGroup() {
        binding.chipGroupMuscle.setOnCheckedStateChangeListener { group, checkedIds ->
            selectedMuscleGroup = if (checkedIds.isNotEmpty()) {
                val chipId = checkedIds.first()
                val chip = group.findViewById<Chip>(chipId)
                chip?.text?.toString()
            } else {
                null
            }
        }
    }

    private fun observeViewModel() {
        viewModel.exercises.observe(viewLifecycleOwner) { exercises ->
            exerciseAdapter.submitList(exercises)
            binding.emptyState.isVisible = exercises.isEmpty()
            binding.exercisesRecycler.isVisible = exercises.isNotEmpty()
        }

        viewModel.exerciseSaved.observe(viewLifecycleOwner) { saved ->
            if (saved) {
                binding.etExerciseName.text?.clear()
                binding.chipGroupMuscle.clearCheck()
                selectedMuscleGroup = null
                viewModel.resetSavedState()
            }
        }
    }

    private fun createExercise() {
        val name = binding.etExerciseName.text?.toString()?.trim()

        if (!name.isNullOrEmpty()) {
            viewModel.createExercise(name, selectedMuscleGroup)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
