package com.example.semestralapp.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.semestralapp.data.Task
import com.example.semestralapp.databinding.ItemTaskBinding

class TaskAdapter(
    private var tasks: List<Task>,
    private val onChecked: (Task) -> Unit,
    private val onDelete: (Task) -> Unit,
    private val onItemClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        holder.binding.textTitle.text = task.title
        holder.binding.textNote.text = task.note
        holder.binding.checkCompleted.isChecked = task.completed

        // Handle photo
        if (!task.photoUrl.isNullOrEmpty()) {
            try {
                val decodedString = Base64.decode(task.photoUrl, Base64.DEFAULT)
                val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                holder.binding.imageTaskPhoto.setImageBitmap(decodedByte)
                holder.binding.imageTaskPhoto.visibility = View.VISIBLE
            } catch (e: Exception) {
                holder.binding.imageTaskPhoto.visibility = View.GONE
            }
        } else {
            holder.binding.imageTaskPhoto.visibility = View.GONE
        }

        holder.binding.checkCompleted.setOnClickListener {
            onChecked(task)
        }

        holder.binding.imageDelete.setOnClickListener {
            onDelete(task)
        }

        holder.itemView.setOnClickListener {
            onItemClick(task)
        }
    }

    override fun getItemCount() = tasks.size

    fun submitList(newList: List<Task>) {
        tasks = newList
        notifyDataSetChanged()
    }
}