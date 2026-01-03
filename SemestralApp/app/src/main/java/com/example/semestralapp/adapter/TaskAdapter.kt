package com.example.semestralapp.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.semestralapp.data.Task
import com.example.semestralapp.databinding.ItemTaskBinding

/**
 * Adaptér pro RecyclerView, který obsluhuje zobrazení seznamu úkolů.
 * Propojuje data (List<Task>) s GUI (item_task.xml).
 */
class TaskAdapter(
    // Seznam úkolů k zobrazení
    private var tasks: List<Task>,
    // Callback fce při clicku na checkbox (změna stavu splněno)
    private val onChecked: (Task) -> Unit,
    // Callback fce při clicku na ikonu koše (smazání)
    private val onDelete: (Task) -> Unit,
    // Callback fce při clicku na celý řádek (otevření detailu/editace)
    private val onItemClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    /**
     * ViewHolder holduje reference na views v jednom řádku seznamu
     * pro bezpečný přístup k prvkům - View Binding
     */
    inner class TaskViewHolder(val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root)

    /**
     * Vytvoří nový ViewHolder - "nafoukne" (inflate) xml layout pro jeden řádek
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TaskViewHolder(binding)
    }

    /**
     * Naplní ViewHolder daty pro konkrétní pozici v seznamu
     */
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        // Nastavení textů
        holder.binding.textTitle.text = task.title
        holder.binding.textNote.text = task.note
        holder.binding.checkCompleted.isChecked = task.completed

        // Process fotky - dekódování z Base64 řetězce zpět na Bitmap
        if (!task.photoUrl.isNullOrEmpty()) {
            try {
                // Transfer String -> Byte Array -> Bitmap
                val decodedString = Base64.decode(task.photoUrl, Base64.DEFAULT)
                val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                
                holder.binding.imageTaskPhoto.setImageBitmap(decodedByte)
                holder.binding.imageTaskPhoto.visibility = View.VISIBLE
            } catch (e: Exception) {
                // Pokud dekódování neprojde, image se skryje
                holder.binding.imageTaskPhoto.visibility = View.GONE
            }
        } else {
            // Pokud úkol nemá fotku, skryje se ImageView
            holder.binding.imageTaskPhoto.visibility = View.GONE
        }

        // Nastavení listenerů (reakce na kliknutí)
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

    /**
     * Returnuje celkový počet položek v seznamu.
     */
    override fun getItemCount() = tasks.size

    /**
     * Metoda pro aktualizaci dat v adaptéru.
     * Callne se, když přijdou nová data z Firestore.
     */
    fun submitList(newList: List<Task>) {
        tasks = newList
        notifyDataSetChanged() // Řekne RecyclerView, aby se překreslil
    }
}