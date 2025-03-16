package com.example.todoapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapplication.databinding.ItemTaskBinding

class TasksAdapter(
    private val onTaskClick: (Task) -> Unit,
    private val onTaskDelete: (Task) -> Unit,
    private val onTaskCheckboxClick: (Task) -> Unit
) :
    ListAdapter<Task, TasksAdapter.TaskItemViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskItemViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskItemViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task, onTaskClick, onTaskDelete,onTaskCheckboxClick)
    }

    class TaskItemViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind( task: Task,
                  onTaskClick: (Task) -> Unit,
                  onTaskDelete: (Task) -> Unit,
                  onTaskCheckboxClick: (Task) -> Unit) {
            binding.taskTitle.text = task.title
            binding.taskDescription.text = task.description
            binding.taskCheckbox.isChecked = task.isCompleted

            binding.root.setOnClickListener {
                onTaskClick(task)
            }

            binding.taskCheckbox.setOnClickListener {
                onTaskCheckboxClick(task)
            }

            binding.taskDelete.setOnClickListener {
                onTaskDelete(task)
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}