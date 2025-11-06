package com.example.myapp011todolist.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp011todolist.data.Todo
import com.example.myapp011todolist.databinding.ItemTodoBinding

class TodoAdapter(
    private val onToggle: (Todo, Boolean) -> Unit,
    private val onEdit: (Todo) -> Unit
) : ListAdapter<Todo, TodoAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<Todo>() {
        override fun areItemsTheSame(a: Todo, b: Todo) = a.id == b.id
        override fun areContentsTheSame(a: Todo, b: Todo) = a == b
    }

    inner class VH(val b: ItemTodoBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inf = LayoutInflater.from(parent.context)
        return VH(ItemTodoBinding.inflate(inf, parent, false))
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val item = getItem(pos)

        h.b.title.text = item.title

        if (!item.description.isNullOrEmpty()) {
            h.b.desc.visibility = View.VISIBLE
            h.b.desc.text = item.description
        } else {
            h.b.desc.visibility = View.GONE
            h.b.desc.text = ""
        }

        h.b.checkBox.setOnCheckedChangeListener(null)
        h.b.checkBox.isChecked = item.done
        h.b.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onToggle(item, isChecked)
        }

        if (!item.imageUri.isNullOrEmpty()) {
            h.b.preview.visibility = View.VISIBLE
            h.b.preview.setImageURI(Uri.parse(item.imageUri))
        } else {
            h.b.preview.visibility = View.GONE
            h.b.preview.setImageDrawable(null)
        }

        h.b.rowRoot.setOnClickListener { onEdit(item) }
    }
}
