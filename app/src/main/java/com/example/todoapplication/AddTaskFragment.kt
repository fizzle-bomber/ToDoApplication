package com.example.todoapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.todoapplication.databinding.FragmentAddTaskBinding

class AddTaskFragment : Fragment() {

    //private lateinit var viewModel: TaskViewModel
    private var taskId = -1
    private val viewModel: TaskViewModel by viewModels {
        TasksViewModelFactory((requireActivity().application as TodoApplication).repository)
    }

        @SuppressLint("SetTextI18n")
        override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding = FragmentAddTaskBinding.inflate(inflater,container,false)

            // Get taskId from arguments
            taskId = arguments?.getInt("taskId", -1) ?: -1

            // Initialize ViewModel
            //viewModel = ViewModelProvider(this).get(TaskViewModel::class.java)

            // Load task details if editing
            if (taskId != -1) {
                binding.buttonSave.text = "Update" // Change button text to "Update"
                viewModel.getTaskById(taskId).observe(viewLifecycleOwner, Observer { task ->
                    binding.editTextTitle.setText(task.title)
                    binding.editTextDescription.setText(task.description)
                })
            } else {
                // Adding a new task
                binding.buttonSave.text = "Save" // Change button text to "Save"
            }

            binding.buttonSave.setOnClickListener {
                val title = binding.editTextTitle.text.toString()
                val description = binding.editTextDescription.text.toString()

                if(title.isNotEmpty() && description.isNotEmpty()){
                    if(taskId == -1){
                        val task = Task(title=title, description = description)
                        viewModel.insert(task)
                    }
                    else{
                        val task = Task(id = taskId, title = title, description = description)
                        viewModel.update(task)
                    }
                    // Navigate back to the task list
                    findNavController().navigateUp()
                } else
                {
                    // Show error if fields are empty
                    Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
        return binding.root
        }

}