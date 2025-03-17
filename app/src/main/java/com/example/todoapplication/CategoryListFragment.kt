package com.example.todoapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapplication.databinding.FragmentCategoryListBinding

class CategoryListFragment : Fragment() {

    private val viewModel: TaskViewModel by viewModels {
        TasksViewModelFactory((requireActivity().application as TodoApplication).repository)
    }

    private lateinit var binding: FragmentCategoryListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView
        val adapter = CategoryAdapter(
            onCategoryClick = { category ->
                // Navigate to edit category screen (optional)
            },
            onCategoryDelete = { category ->
                viewModel.deleteCategory(category)
            }
        )
        binding.recyclerViewCategories.adapter = adapter
        binding.recyclerViewCategories.layoutManager = LinearLayoutManager(requireContext())

        // Observe categories from ViewModel
        viewModel.allCategories.observe(viewLifecycleOwner, Observer { categories ->
            categories?.let { adapter.submitList(it) }
        })

        // Handle add category button click
        binding.buttonAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }
    }

    private fun showAddCategoryDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_category, null)
        val input = dialogView.findViewById<EditText>(R.id.editTextCategoryName)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Category")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val categoryName = input.text?.toString()
                if (!categoryName.isNullOrEmpty()) {
                    val category = Category(name = categoryName)
                    viewModel.insertCategory(category)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }
}