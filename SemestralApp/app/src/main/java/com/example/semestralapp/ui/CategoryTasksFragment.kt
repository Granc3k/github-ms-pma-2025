package com.example.semestralapp.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.semestralapp.R
import com.example.semestralapp.adapter.TaskAdapter
import com.example.semestralapp.data.Category
import com.example.semestralapp.data.Task
import com.example.semestralapp.databinding.FragmentCategoryTasksBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.io.ByteArrayOutputStream

/**
 * Fragment pro zobrazení úkolů v konkrétní selectnuté kategorii
 * Přijímá ID kategorie jako argument při navigaci.
 */
class CategoryTasksFragment : Fragment() {

    private var _binding: FragmentCategoryTasksBinding? = null
    private val binding get() = _binding!!
    
    // Instance Firestore
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: TaskAdapter
    
    // Getnutí argumentů předaných při navigaci (categoryId, categoryName)
    // Je vyžtadovanej plugin "androidx.navigation.safeargs.kotlin" v build.gradle
    private val args: CategoryTasksFragmentArgs by navArgs()

    // Vars pro obrázek
    private var selectedImageBitmap: Bitmap? = null
    private lateinit var dialogImagePreview: ImageView
    
    // List kategorií pro Spinner v edit dialogu
    private var categoriesList: MutableList<Category> = mutableListOf()

    // Registr aktivity pro choose obrázku
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                selectedImageBitmap = getResizedBitmap(bitmap, 800)
                dialogImagePreview.setImageBitmap(selectedImageBitmap)
                dialogImagePreview.visibility = View.VISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Chyba při načítání obrázku", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        db = FirebaseFirestore.getInstance()
        
        // Init adaptéru - stejný jako v HomeFragment
        adapter = TaskAdapter(
            tasks = emptyList(),
            onChecked = { task -> toggleCompleted(task) },
            onDelete = { task -> deleteTask(task) },
            onItemClick = { task -> showEditTaskDialog(task) } // Click otevře editační dialog
        )

        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewTasks.adapter = adapter

        binding.fabAddTask.setOnClickListener {
            showAddTaskDialog()
        }

        listenForTasks()
        fetchCategories() // Je potřeba kategorie pro editační dialog
    }

    /**
     * Watchuje úkoly, které patří POUZE do vybrané kategorie (categoryId z argumentů)
     */
    private fun listenForTasks() {
        db.collection("tasks")
            .whereEqualTo("categoryId", args.categoryId) // Filtrace podle kategorie
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                val taskList = snapshots?.toObjects(Task::class.java) ?: emptyList()
                adapter.submitList(taskList)
            }
    }
    
    /**
     * Load kategorií pro Spinner (stejné jako v HomeFragment)
     */
    private fun fetchCategories() {
        db.collection("categories")
            .orderBy("name")
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener
                
                val fetchedCategories = snapshots?.toObjects(Category::class.java) ?: mutableListOf()
                categoriesList.clear()
                categoriesList.add(Category(id = "default", name = "Vše"))
                categoriesList.addAll(fetchedCategories)
            }
    }

    private fun addTask(title: String, note: String, photoBase64: String?) {
        // Tady se fixně nastavujeme categoryId na tu, ve které se právě nacházíme
        val task = Task(
            title = title,
            note = note,
            categoryId = args.categoryId,
            photoUrl = photoBase64,
            completed = false
        )
        db.collection("tasks").add(task)
    }
    
    private fun updateTask(task: Task, title: String, note: String, categoryId: String, photoBase64: String?) {
        if (task.id.isNotEmpty()) {
            val updates = hashMapOf<String, Any>(
                "title" to title,
                "note" to note,
                "categoryId" to categoryId
            )
            if (photoBase64 != null) {
                updates["photoUrl"] = photoBase64
            }
            
            db.collection("tasks").document(task.id).update(updates)
        }
    }

    private fun toggleCompleted(task: Task) {
        if (task.id.isNotEmpty()) {
            db.collection("tasks").document(task.id).update("completed", !task.completed)
        }
    }

    private fun deleteTask(task: Task) {
        if (task.id.isNotEmpty()) {
            db.collection("tasks").document(task.id).delete()
        }
    }

    /**
     * Dialog pro addnutí úkolu v kontextu konkrétní kategorie.
     * Zde se kategorie nevybírá, je předem dána.
     */
    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_task, null)
        val editTitle = dialogView.findViewById<EditText>(R.id.editTaskTitle)
        val editNote = dialogView.findViewById<EditText>(R.id.editTaskNote)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val btnPhoto = dialogView.findViewById<Button>(R.id.btnSelectPhoto)
        dialogImagePreview = dialogView.findViewById(R.id.imagePreview)
        
        // Spinner pro výběr kategorie se skryje, protože addujeme úkol přímo do té kategorie
        spinnerCategory.visibility = View.GONE
        
        selectedImageBitmap = null

        btnPhoto.setOnClickListener {
            getContent.launch("image/*")
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Přidat úkol do: ${args.categoryName}")
            .setView(dialogView)
            .setPositiveButton("Přidat") { _, _ ->
                val title = editTitle.text.toString()
                val note = editNote.text.toString()
                
                var photoBase64: String? = null
                if (selectedImageBitmap != null) {
                    photoBase64 = bitmapToBase64(selectedImageBitmap!!)
                }

                if (title.isNotEmpty()) {
                    addTask(title, note, photoBase64)
                } else {
                    Toast.makeText(context, "Název nesmí být prázdný", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Zrušit", null)
            .show()
    }

    /**
     * Dialog pro editac úkolu
     * Zde se umožňuje změnit kategorii, i když je uživatel v detailu jiné kategorie
     * Pokud uživatel změní kategorii, úkol zmizí z aktuálního seznamu (protože přestane splňovat filtr).
     */
    private fun showEditTaskDialog(task: Task) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_task_detail, null)
        val editTitle = dialogView.findViewById<TextInputEditText>(R.id.editTaskTitle)
        val editNote = dialogView.findViewById<TextInputEditText>(R.id.editTaskNote)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val btnPhoto = dialogView.findViewById<Button>(R.id.btnSelectPhoto)
        dialogImagePreview = dialogView.findViewById(R.id.detailImage)
        
        editTitle.setText(task.title)
        editNote.setText(task.note)
        
        // Setting Spinneru kategorií
        val categoryNames = categoriesList.map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
        
        // Předvybrání aktuální kategorie
        val currentCategoryIndex = categoriesList.indexOfFirst { it.id == task.categoryId }
        if (currentCategoryIndex != -1) {
            spinnerCategory.setSelection(currentCategoryIndex)
        }

        selectedImageBitmap = null
        
        if (!task.photoUrl.isNullOrEmpty()) {
             try {
                val decodedString = Base64.decode(task.photoUrl, Base64.DEFAULT)
                val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                dialogImagePreview.setImageBitmap(decodedByte)
                dialogImagePreview.visibility = View.VISIBLE
            } catch (e: Exception) {
                dialogImagePreview.visibility = View.GONE
            }
        }

        btnPhoto.setOnClickListener {
            getContent.launch("image/*")
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Upravit úkol")
            .setView(dialogView)
            .setPositiveButton("Uložit") { _, _ ->
                val title = editTitle.text.toString()
                val note = editNote.text.toString()
                
                val selectedCategoryPosition = spinnerCategory.selectedItemPosition
                val categoryId = if (selectedCategoryPosition >= 0 && selectedCategoryPosition < categoriesList.size) {
                    categoriesList[selectedCategoryPosition].id
                } else {
                    "default"
                }
                
                var photoBase64: String? = null
                if (selectedImageBitmap != null) {
                    photoBase64 = bitmapToBase64(selectedImageBitmap!!)
                }

                if (title.isNotEmpty()) {
                    updateTask(task, title, note, categoryId, photoBase64)
                } else {
                    Toast.makeText(context, "Název nesmí být prázdný", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Zrušit", null)
            .show()
    }

    private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}