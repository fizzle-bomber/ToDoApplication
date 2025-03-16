package com.example.todoapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapplication.databinding.FragmentTaskBinding


class TaskFragment : Fragment() {

    private lateinit var taskAdapter: TasksAdapter
    private val viewModel: TaskViewModel by viewModels {
        TasksViewModelFactory((requireActivity().application as TodoApplication).repository)
    }

    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

// Initialize Adapter
        taskAdapter = TasksAdapter(
            onTaskClick = { task ->
                // Navigate to AddEditTaskFragment for editing
                val action = TaskFragmentDirections.actionTaskFragmentToAddTaskFragment(task.id)
                findNavController().navigate(action)
            },
            onTaskDelete = { task ->
                // Delete the task
                viewModel.delete(task.id)
            },
            onTaskCheckboxClick = { task ->
                // Toggle completion status
                task.isCompleted = !task.isCompleted
                viewModel.update(task)
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = taskAdapter

        viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            tasks?.let { taskAdapter.submitList(it) }
        }

        // Handle FAB click (add new task)
        binding.fab.setOnClickListener {
            val action = TaskFragmentDirections.actionTaskFragmentToAddTaskFragment(-1)
            findNavController().navigate(action)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}