package com.example.myapp011todolist.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp011todolist.databinding.ActivityMainBinding
import com.example.myapp011todolist.databinding.DialogAddEditTodoBinding
import com.example.myapp011todolist.vm.TodoViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    private val vm: TodoViewModel by viewModels()

    // Pomocny callback: ktery dialog ma dostat vysledek vyberu obrazku
    private var deliverPickedImage: ((Uri?) -> Unit)? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            // Persist permission, at je URI pristupne i po restartu
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
                // na nekterych zarizenich neni treba / neni mozne
            }
        }
        deliverPickedImage?.invoke(uri)
        deliverPickedImage = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        val adapter = TodoAdapter(
            onToggle = { todo, checked -> vm.toggle(todo.id, checked) },
            onEdit = { todo ->
                showAddEditTodoDialog(
                    initialTitle = todo.title,
                    initialDesc = todo.description,
                    initialImage = todo.imageUri?.let(Uri::parse)
                ) { newTitle, newDesc, newImage ->
                    vm.editWith(todo.id, newTitle, newDesc, newImage?.toString())
                }
            }
        )
        b.recycler.adapter = adapter

        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, tgt: RecyclerView.ViewHolder) = false
            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                val item = adapter.currentList[vh.bindingAdapterPosition]
                vm.remove(item.id)
            }
        }).attachToRecyclerView(b.recycler)

        b.fab.setOnClickListener {
            showAddEditTodoDialog(initialTitle = null, initialDesc = null, initialImage = null) { title, desc, image ->
                vm.addWith(title, desc, image?.toString())
            }
        }

        b.btnClearDone.setOnClickListener {
            vm.clearDone()
            Toast.makeText(this, "Hotove ukoly byly smazany", Toast.LENGTH_SHORT).show()
        }

        vm.items.observe(this) { adapter.submitList(it) }
    }

    /**
     * Dialog pro pridani/upravu: nazev, popis, vyber fotky.
     * onSubmit vraci (title, description, imageUri)
     */
    private fun showAddEditTodoDialog(
        initialTitle: String?,
        initialDesc: String?,
        initialImage: Uri?,
        onSubmit: (String, String?, Uri?) -> Unit
    ) {
        val bind = DialogAddEditTodoBinding.inflate(layoutInflater)
        bind.input.setText(initialTitle ?: "")
        bind.inputDesc.setText(initialDesc ?: "")

        var pickedUri: Uri? = initialImage
        if (pickedUri != null) {
            bind.preview.visibility = android.view.View.VISIBLE
            bind.preview.setImageURI(pickedUri)
        } else {
            bind.preview.visibility = android.view.View.GONE
        }

        bind.btnPick.setOnClickListener {
            deliverPickedImage = { uri ->
                pickedUri = uri
                if (uri != null) {
                    bind.preview.visibility = android.view.View.VISIBLE
                    bind.preview.setImageURI(uri)
                } else {
                    bind.preview.visibility = android.view.View.GONE
                    bind.preview.setImageDrawable(null)
                }
            }
            pickImage.launch(arrayOf("image/*"))
        }

        AlertDialog.Builder(this)
            .setTitle(if (initialTitle == null) "Novy ukol" else "Upravit ukol")
            .setView(bind.root)
            .setPositiveButton("OK") { _, _ ->
                val title = bind.input.text?.toString()?.trim().orEmpty()
                val desc = bind.inputDesc.text?.toString()?.trim().orEmpty().ifEmpty { null }
                if (title.isNotEmpty()) onSubmit(title, desc, pickedUri)
            }
            .setNegativeButton("Zrusit", null)
            .show()
    }
}
