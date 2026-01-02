package com.example.myapp014asharedtasklist

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.myapp014asharedtasklist.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: FirebaseFirestore

    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.firestore

        // Nastavení adapteru
        adapter = TaskAdapter(
            tasks = emptyList(),
            onChecked = { task, newState -> toggleCompleted(task, newState) },
            onDelete = { task -> deleteTask(task) },
            onEdit = { task -> showEditDialog(task) }
        )

        binding.recyclerViewTasks.adapter = adapter
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(this)

        // Přidání úkolu
        binding.buttonAdd.setOnClickListener {
            val title = binding.inputTask.text.toString()
            if (title.isNotEmpty()) {
                addTask(title)
                binding.inputTask.text.clear()
            }
        }

        // Realtime sledování Firestore
        listenForTasks()
    }

    private fun addTask(title: String) {
        println("DEBUG: addTask called with title = $title")
        val task = Task(title = title, completed = false)
        db.collection("tasks").add(task)
    }

    private fun toggleCompleted(task: Task, newState: Boolean) {
        // Aktualizuj konkrétní dokument podle id
        if (task.id.isNotEmpty()) {
            db.collection("tasks").document(task.id)
                .update("completed", newState)
        } else {
            // fallback: pokud id chybí, hledáme podle title (méně přesné)
            db.collection("tasks")
                .whereEqualTo("title", task.title)
                .get()
                .addOnSuccessListener { docs ->
                    for (doc in docs) {
                        db.collection("tasks").document(doc.id).update("completed", newState)
                    }
                }
        }
    }

    private fun deleteTask(task: Task) {
        if (task.id.isNotEmpty()) {
            db.collection("tasks").document(task.id).delete()
        } else {
            db.collection("tasks")
                .whereEqualTo("title", task.title)
                .get()
                .addOnSuccessListener { docs ->
                    for (doc in docs) db.collection("tasks").document(doc.id).delete()
                }
        }
    }


    private fun listenForTasks() {
        db.collection("tasks")
            // Sleduje kolekci tasks v reálném čase
            .addSnapshotListener { snapshots, _ ->
                // Převede dokumenty z Firestore na seznam objektů Task a připojí document id
                val taskList = snapshots?.documents?.mapNotNull { doc ->
                    val t = doc.toObject(Task::class.java)
                    t?.copy(id = doc.id)
                } ?: emptyList()
                // Aktualizuje RecyclerView novým seznamem úkolů
                adapter.submitList(taskList)
            }
    }

    private fun showEditDialog(task: Task) {
        val edit = EditText(this)
        edit.setText(task.title)
        AlertDialog.Builder(this)
            .setTitle("Upravit úkol")
            .setView(edit)
            .setPositiveButton("Uložit") { _, _ ->
                val newTitle = edit.text.toString()
                if (task.id.isNotEmpty()) {
                    db.collection("tasks").document(task.id).update("title", newTitle)
                } else {
                    // fallback: update all with same title
                    db.collection("tasks").whereEqualTo("title", task.title).get()
                        .addOnSuccessListener { docs ->
                            for (doc in docs) db.collection("tasks").document(doc.id).update("title", newTitle)
                        }
                }
            }
            .setNegativeButton("Zrušit", null)
            .show()
    }
}