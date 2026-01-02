package com.example.semestralapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.semestralapp.data.Category
import com.example.semestralapp.databinding.ItemCategoryBinding

class CategoryAdapter(
    private var categories: List<Category>,
    private val onClick: (Category) -> Unit,
    private val onEdit: (Category) -> Unit,
    private val onDelete: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.textCategoryName.text = category.name

        holder.itemView.setOnClickListener {
            onClick(category)
        }

        holder.binding.imageEdit.setOnClickListener {
            onEdit(category)
        }

        holder.binding.imageDelete.setOnClickListener {
            onDelete(category)
        }
    }

    override fun getItemCount() = categories.size

    fun submitList(newList: List<Category>) {
        categories = newList
        notifyDataSetChanged()
    }
}