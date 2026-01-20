package com.example.workouttracker.ui.screens.workout

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
import com.example.workouttracker.data.local.entity.Exercise
import com.example.workouttracker.databinding.FragmentWorkoutBinding
import com.example.workouttracker.databinding.DialogAddSetBinding
import com.example.workouttracker.databinding.BottomSheetExercisesBinding
import com.example.workouttracker.ui.adapters.ExerciseCardAdapter
import com.example.workouttracker.ui.adapters.ExercisePickerAdapter
import com.example.workouttracker.ui.adapters.ExerciseWithSets
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class WorkoutFragment : Fragment() {

    private var _binding: FragmentWorkoutBinding? = null
    private val binding get() = _binding!!

    private val workoutId: Long by lazy {
        arguments?.getLong(ARG_WORKOUT_ID) ?: 0L
    }

    private val viewModel: WorkoutViewModel by viewModels {
        val app = requireActivity().application as WorkoutApplication
        WorkoutViewModel.factory(app.container.workoutRepository, workoutId)
    }

    private lateinit var exerciseCardAdapter: ExerciseCardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView() {
        exerciseCardAdapter = ExerciseCardAdapter(
            onAddSetClick = { exercise ->
                showAddSetDialog(exercise)
            },
            onDeleteSetClick = { set ->
                viewModel.deleteSet(set)
            },
            onRemoveExerciseClick = { exercise ->
                viewModel.removeExerciseFromWorkout(exercise.id)
            }
        )

        binding.exercisesRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = exerciseCardAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnAddExercise.setOnClickListener {
            showExercisePickerBottomSheet()
        }

        binding.btnCompleteWorkout.setOnClickListener {
            viewModel.completeWorkout()
            parentFragmentManager.popBackStack()
        }
    }

    private fun observeViewModel() {
        viewModel.workout.observe(viewLifecycleOwner) { workout ->
            workout?.let {
                binding.toolbarTitle.text = it.name
                binding.toolbarSubtitle.isVisible = it.isCompleted
                binding.btnAddExercise.isVisible = !it.isCompleted
                binding.btnCompleteWorkout.isVisible = !it.isCompleted
            }
        }

        viewModel.exercises.observe(viewLifecycleOwner) { exercises ->
            updateExerciseCards(exercises)
        }

        viewModel.workoutSets.observe(viewLifecycleOwner) { sets ->
            viewModel.exercises.value?.let { exercises ->
                updateExerciseCards(exercises)
            }
        }
    }

    private fun updateExerciseCards(exercises: List<Exercise>) {
        val sets = viewModel.workoutSets.value ?: emptyList()
        val exerciseIds = sets.map { it.exerciseId }.distinct()

        val exercisesWithSets = exerciseIds.mapNotNull { exerciseId ->
            exercises.find { it.id == exerciseId }?.let { exercise ->
                ExerciseWithSets(
                    exercise = exercise,
                    sets = sets.filter { it.exerciseId == exerciseId }
                )
            }
        }

        exerciseCardAdapter.submitList(exercisesWithSets)

        val hasExercises = exercisesWithSets.isNotEmpty()
        binding.emptyState.isVisible = !hasExercises
        binding.exercisesRecycler.isVisible = hasExercises

        val isCompleted = viewModel.workout.value?.isCompleted == true
        binding.btnCompleteWorkout.isVisible = hasExercises && !isCompleted
    }

    private fun showAddSetDialog(exercise: Exercise) {
        val dialogBinding = DialogAddSetBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.Theme_WorkoutTracker)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnSave.setOnClickListener {
            val reps = dialogBinding.etReps.text.toString().toIntOrNull()
            val weight = dialogBinding.etWeight.text.toString().toDoubleOrNull()

            if (reps != null && reps > 0) {
                viewModel.addSet(exercise.id, reps, weight)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showExercisePickerBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val sheetBinding = BottomSheetExercisesBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(sheetBinding.root)

        var selectedMuscleGroup: String? = null
        var allExercises: List<Exercise> = emptyList()

        val pickerAdapter = ExercisePickerAdapter { exercise ->
            viewModel.addSet(exercise.id, 10, null)
            bottomSheetDialog.dismiss()
        }

        sheetBinding.exercisesRecycler.apply {
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 2)
            adapter = pickerAdapter
        }

        fun updateFilteredList() {
            val filtered = if (selectedMuscleGroup == null) {
                allExercises
            } else {
                allExercises.filter { it.muscleGroup == selectedMuscleGroup }
            }
            pickerAdapter.submitList(filtered)
            sheetBinding.emptyState.isVisible = filtered.isEmpty()
            sheetBinding.exercisesRecycler.isVisible = filtered.isNotEmpty()

            val selectedIds = viewModel.workoutSets.value
                ?.map { it.exerciseId }
                ?.toSet()
                ?: emptySet()
            pickerAdapter.setSelectedExerciseIds(selectedIds)
        }

        sheetBinding.chipGroupFilter.setOnCheckedStateChangeListener { group, checkedIds ->
            selectedMuscleGroup = if (checkedIds.isNotEmpty()) {
                val chipId = checkedIds.first()
                if (chipId == sheetBinding.chipAll.id) {
                    null
                } else {
                    val chip = group.findViewById<Chip>(chipId)
                    chip?.text?.toString()
                }
            } else {
                null
            }
            updateFilteredList()
        }

        viewModel.exercises.observe(viewLifecycleOwner) { exercises ->
            allExercises = exercises
            updateFilteredList()
        }

        bottomSheetDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_WORKOUT_ID = "workout_id"

        fun newInstance(workoutId: Long): WorkoutFragment {
            return WorkoutFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_WORKOUT_ID, workoutId)
                }
            }
        }
    }
}
