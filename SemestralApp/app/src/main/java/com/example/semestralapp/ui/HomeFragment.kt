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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.semestralapp.R
import com.example.semestralapp.adapter.TaskAdapter
import com.example.semestralapp.data.Category
import com.example.semestralapp.data.Task
import com.example.semestralapp.databinding.FragmentHomeBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.io.ByteArrayOutputStream

/**
 * Home fragment aplikace (Domovská obrazovka)
 * Zobrazuje list všech úkolů seřazených podle času vytvoření
 */
class HomeFragment : Fragment() {

    // ViewBinding proměnné pro přístup k layoutu fragment_home.xml
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Instance dbs Firestore
    private lateinit var db: FirebaseFirestore
    // Adaptér pro list úkolů
    private lateinit var adapter: TaskAdapter

    // Vars pro práci s imagem při addu/editu úkolu
    private var selectedImageBitmap: Bitmap? = null
    private lateinit var dialogImagePreview: ImageView
    
    // List kategorií pro naplnění Spinneru (rozbalovacího menu)
    private var categoriesList: MutableList<Category> = mutableListOf()

    // Registr pro výsledek aktivity - výběr image z galerie
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            try {
                // Load obrázku ze zvoleného URI
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                
                // Zmenšení obrázku, aby nezabíral moc paměti a dal se uložit do Firestore
                selectedImageBitmap = getResizedBitmap(bitmap, 800)
                
                // Print náhledu v dialogu
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
        // Init bindingu
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Getnutí instance Firestore
        db = FirebaseFirestore.getInstance()

        // Setting adaptéru pro RecyclerView
        adapter = TaskAdapter(
            tasks = emptyList(),
            onChecked = { task -> toggleCompleted(task) }, // Callback změna stavu
            onDelete = { task -> deleteTask(task) },       // Callback smazání
            onItemClick = { task -> showEditTaskDialog(task) } // Callback editace
        )

        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewTasks.adapter = adapter

        // btn pro addnutí nového úkolu
        binding.fabAddTask.setOnClickListener {
            showAddTaskDialog()
        }

        // Start listeneru změn v dbs
        listenForTasks()
        fetchCategories()
    }

    /**
     * Watchuje změny v kolekci "tasks" v reálném čase
     */
    private fun listenForTasks() {
        db.collection("tasks")
            .orderBy("timestamp", Query.Direction.DESCENDING) // Řazení od nejnovějších
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                // Transfer dokumentů na objekty Task a update listu
                val taskList = snapshots?.toObjects(Task::class.java) ?: emptyList()
                adapter.submitList(taskList)
            }
    }
    
    /**
     * Loadne seznam kategorií pro použití ve Spinneru
     */
    private fun fetchCategories() {
        db.collection("categories")
            .orderBy("name")
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener
                
                val fetchedCategories = snapshots?.toObjects(Category::class.java) ?: mutableListOf()
                categoriesList.clear()
                // Ruční přidání výchozí kategorie "Vše"
                categoriesList.add(Category(id = "default", name = "Vše"))
                categoriesList.addAll(fetchedCategories)
            }
    }

    /**
     * Addne nový úkol do Firestore.
     */
    private fun addTask(title: String, note: String, categoryId: String, photoBase64: String?) {
        val task = Task(
            title = title,
            note = note,
            categoryId = categoryId,
            photoUrl = photoBase64,
            completed = false
        )
        db.collection("tasks").add(task)
    }
    
    /**
     * Update existující úkol ve Firestore.
     */
    private fun updateTask(task: Task, title: String, note: String, categoryId: String, photoBase64: String?) {
        if (task.id.isNotEmpty()) {
            val updates = hashMapOf<String, Any>(
                "title" to title,
                "note" to note,
                "categoryId" to categoryId
            )
            // Update fotky pouze pokud byla vybrána nová
            if (photoBase64 != null) {
                updates["photoUrl"] = photoBase64
            }
            
            db.collection("tasks").document(task.id).update(updates)
        }
    }

    /**
     * Switchne stav splnění úkolu (completed)
     */
    private fun toggleCompleted(task: Task) {
        if (task.id.isNotEmpty()) {
            db.collection("tasks").document(task.id).update("completed", !task.completed)
        }
    }

    /**
     * Deletne úkol z databáze
     */
    private fun deleteTask(task: Task) {
        if (task.id.isNotEmpty()) {
            db.collection("tasks").document(task.id).delete()
        }
    }

    /**
     * Printne dialog pro vytvoření nového úkolu
     */
    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_task, null)
        val editTitle = dialogView.findViewById<EditText>(R.id.editTaskTitle)
        val editNote = dialogView.findViewById<EditText>(R.id.editTaskNote)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val btnPhoto = dialogView.findViewById<Button>(R.id.btnSelectPhoto)
        dialogImagePreview = dialogView.findViewById(R.id.imagePreview)
        
        // Setting adaptéru pro Spinner (výběr kategorie)
        val categoryNames = categoriesList.map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
        
        selectedImageBitmap = null

        // btn pro výběr fotky
        btnPhoto.setOnClickListener {
            getContent.launch("image/*")
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Přidat úkol")
            .setView(dialogView)
            .setPositiveButton("Přidat") { _, _ ->
                val title = editTitle.text.toString()
                val note = editNote.text.toString()
                
                // Getnutí selectnuté kategorie ze Spinneru
                val selectedCategoryPosition = spinnerCategory.selectedItemPosition
                val categoryId = if (selectedCategoryPosition >= 0 && selectedCategoryPosition < categoriesList.size) {
                    categoriesList[selectedCategoryPosition].id
                } else {
                    "default"
                }
                
                // Convert vybrané fotky na Base64
                var photoBase64: String? = null
                if (selectedImageBitmap != null) {
                    photoBase64 = bitmapToBase64(selectedImageBitmap!!)
                }

                if (title.isNotEmpty()) {
                    addTask(title, note, categoryId, photoBase64)
                } else {
                    Toast.makeText(context, "Název nesmí být prázdný", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Zrušit", null)
            .show()
    }

    /**
     * Printne dialog pro edit existujícího úkolu
     */
    private fun showEditTaskDialog(task: Task) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_task_detail, null)
        val editTitle = dialogView.findViewById<TextInputEditText>(R.id.editTaskTitle)
        val editNote = dialogView.findViewById<TextInputEditText>(R.id.editTaskNote)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val btnPhoto = dialogView.findViewById<Button>(R.id.btnSelectPhoto)
        dialogImagePreview = dialogView.findViewById(R.id.detailImage)
        
        // Předvyplnění polí
        editTitle.setText(task.title)
        editNote.setText(task.note)
        
        // Setting Spinneru
        val categoryNames = categoriesList.map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
        
        // Setting vybrané kategorie ve Spinneru
        val currentCategoryIndex = categoriesList.indexOfFirst { it.id == task.categoryId }
        if (currentCategoryIndex != -1) {
            spinnerCategory.setSelection(currentCategoryIndex)
        }

        selectedImageBitmap = null 
        
        // Load existující fotky
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

    /**
     * Pomocná fce pro zmenšení bitmapy (obrázku)
     * šetří to paměť na limity daný Firestorem (dokument max 1MB)
     */
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

    /**
     * Transfer bitmapy na Base64 String pro save do dbs
     */
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