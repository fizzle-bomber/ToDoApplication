package com.example.todoapplication

import android.R
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.todoapplication.databinding.FragmentAddTaskBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class AddTaskFragment : Fragment() {

    //private lateinit var viewModel: TaskViewModel
    private lateinit var binding: FragmentAddTaskBinding

    private var taskId = -1
    private var selectedCategoryId: Int? = null

    private val viewModel: TaskViewModel by viewModels {
        TasksViewModelFactory((requireActivity().application as TodoApplication).repository)
    }
    private var dueDate: Date? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAddTaskBinding.inflate(inflater, container, false)

        // Get taskId from arguments
        taskId = arguments?.getInt("taskId", -1) ?: -1


        // Load categories into the Spinner
        viewModel.allCategories.observe(viewLifecycleOwner, Observer { categories ->
            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.simple_spinner_item,
                categories.map { it.name }
            )
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = adapter

            // Set selected category if editing a task
            if (taskId != -1) {
                viewModel.getTaskById(taskId).observe(viewLifecycleOwner, Observer { task ->
                    task?.let {
                        binding.editTextTitle.setText(it.title)
                        binding.editTextDescription.setText(it.description)
                        dueDate = it.dueDate
                        selectedCategoryId = it.categoryId
                        updateDueDateButtonText()

                        // Set the selected category in the Spinner
                        val categoryPosition =
                            categories.indexOfFirst { category -> category.id == selectedCategoryId }
                        if (categoryPosition != -1) {
                            binding.spinnerCategory.setSelection(categoryPosition)
                        }
                    }
                })
            }
            else {
                // Adding a new task
                binding.buttonSave.text = "Save" // Change button text to "Save"
            }
        })


        // Handle due date button click
        binding.buttonDueDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.buttonSave.setOnClickListener {


            val title = binding.editTextTitle.text.toString()
            val description = binding.editTextDescription.text.toString()
            val selectedCategory =
                viewModel.allCategories.value?.get(binding.spinnerCategory.selectedItemPosition)

            // Inside the save/update button click listener
            if (dueDate != null) {
                val timeUntilDue = dueDate!!.time - System.currentTimeMillis()
                if (timeUntilDue > 0) {
                    val reminderWorkRequest = OneTimeWorkRequest.Builder(ReminderWorker::class.java)
                        .setInputData(workDataOf("taskTitle" to title))
                        .setInitialDelay(timeUntilDue, TimeUnit.MILLISECONDS)
                        .build()

                    WorkManager.getInstance(requireContext()).enqueue(reminderWorkRequest)
                }
            }

            if (title.isNotEmpty() && description.isNotEmpty()) {
                if (taskId == -1) {
                    val task = Task(
                        title = title,
                        description = description,
                        dueDate = dueDate,
                        categoryId = selectedCategory?.id
                    )
                    viewModel.insert(task)
                } else {
                    val task = Task(
                        id = taskId,
                        title = title,
                        description = description,
                        dueDate = dueDate
                    )
                    viewModel.update(task)
                }
                // Navigate back to the task list
                findNavController().navigateUp()
            } else {
                // Show error if fields are empty
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        return binding.root
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }
                dueDate = selectedDate.time
                updateDueDateButtonText()
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun updateDueDateButtonText() {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        binding.buttonDueDate.text = dueDate?.let { dateFormat.format(it) } ?: "Set Due Date"
    }

}