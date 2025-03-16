package com.example.todoapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapplication.databinding.FragmentTaskBinding

class TaskFragment : Fragment() {

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

        val adapter = TasksAdapter { task ->
            //Handle task click
            viewModel.update(task.copy(isCompleted = !task.isCompleted))
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
            tasks?.let { adapter.submitList(it) }
        }

        binding.fab.setOnClickListener {
            val task = Task(title = "New Task", description = "Task Description")
            viewModel.insert(task)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}