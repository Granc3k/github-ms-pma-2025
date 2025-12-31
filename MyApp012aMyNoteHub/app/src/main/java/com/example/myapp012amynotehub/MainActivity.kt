package com.example.myapp012amynotehub

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapp012amynotehub.data.Note
import com.example.myapp012amynotehub.data.NoteDao
import com.example.myapp012amynotehub.data.NoteHubDatabaseInstance
import com.example.myapp012amynotehub.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var noteDao: NoteDao
    private lateinit var adapter: NoteAdapter
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Po kliknutí na FAB otevře AddNoteActivity
        binding.fabAddNote.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }

        /* Zobrazování poznámek */

        // 1) Vytvoření adaptéru
        //val adapter = NoteAdapter()
        adapter = NoteAdapter(
            onEditClick = { note ->
                val intent = Intent(this, EditNoteActivity::class.java)
                intent.putExtra("note_id", note.id)
                startActivity(intent)
            },
            onDeleteClick = { note ->
                deleteNote(note)
            }
        )


        // 2) Nastavení RecyclerView
        binding.recyclerViewNotes.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewNotes.adapter = adapter

        // 3) Získání DAO
        //val noteDao = NoteHubDatabaseInstance.getDatabase(applicationContext).noteDao()
        noteDao = NoteHubDatabaseInstance.getDatabase(this).noteDao()

        // 4) Search input handling: spouštíme sběr dat podle dotazu
        binding.editTextSearch.addTextChangedListener { editable ->
            val q = editable?.toString() ?: ""
            startSearch(q)
        }

        // start with empty query to load all notes
        startSearch("")



    }
    private fun deleteNote(note: Note) {
        // spustí se asynchronní úkol na vlákně určeném pro práci s databází
        // a ukončí se automaticky, když se aktivita zničí
        lifecycleScope.launch(Dispatchers.IO) {
            noteDao.delete(note)
        }
    }

    private fun startSearch(query: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            val flow = if (query.isBlank()) noteDao.getAllNotes() else noteDao.search("%" + query + "%")
            flow.collectLatest { notes ->
                withContext(Dispatchers.Main) {
                    adapter.submitList(notes)
                }
            }
        }
    }
}