package com.example.semestralapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.semestralapp.R
import com.example.semestralapp.adapter.CategoryAdapter
import com.example.semestralapp.data.Category
import com.example.semestralapp.databinding.FragmentCategoriesBinding
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Fragment pro správu kategorií
 * Zobrazuje seznam všech kategorií, umožňuje jejich addnutí, edit a delete
 */
class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    
    // Instance Firestore
    private lateinit var db: FirebaseFirestore
    // Adaptér pro seznam kategorií
    private lateinit var adapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        db = FirebaseFirestore.getInstance()

        adapter = CategoryAdapter(
            categories = emptyList(),
            // Po clicku na kategorii přejdeme na seznam úkolů v dané kategorii
            onClick = { category -> 
                val action = CategoriesFragmentDirections.actionCategoriesFragmentToCategoryTasksFragment(
                    categoryId = category.id,
                    categoryName = category.name
                )
                findNavController().navigate(action)
            },
            // Callback pro edit
            onEdit = { category -> 
                // Speciální kontrola - výchozí kategorii nelze upravit
                if (category.id == "default") {
                    Toast.makeText(context, "Výchozí kategorii nelze upravit", Toast.LENGTH_SHORT).show()
                } else {
                    showEditCategoryDialog(category) 
                }
            },
            // Callback pro delete
            onDelete = { category -> 
                // Speciální kontrola - výchozí kategorii nelze smazat
                if (category.id == "default") {
                    Toast.makeText(context, "Výchozí kategorii nelze smazat", Toast.LENGTH_SHORT).show()
                } else {
                    deleteCategory(category)
                }
            }
        )

        binding.recyclerViewCategories.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewCategories.adapter = adapter

        binding.fabAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }

        listenForCategories()
    }

    /**
     * Watchuje změny v kolekci "categories".
     */
    private fun listenForCategories() {
        db.collection("categories")
            .orderBy("name")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                val categoryList = snapshots?.toObjects(Category::class.java) ?: mutableListOf()
                
                val fullList = mutableListOf<Category>()
                // Ručně přidá výchozí kategorii "Vše" na začátek seznamu
                // Tahle kategorie fyzicky neexistuje v kolekci "categories", ale reprezentuje filtr na úkoly s categoryId="default"
                // Tohle bylo jediný co mi fachčilo a nechtělo se mi to dál řešit :D
                fullList.add(Category(id = "default", name = "Vše"))
                fullList.addAll(categoryList)
                
                adapter.submitList(fullList)
            }
    }

    /**
     * Creatne novou kategorii v dbs
     */
    private fun addCategory(name: String) {
        val category = Category(name = name)
        db.collection("categories").add(category)
    }

    /**
     * Editne název existující kategorie
     */
    private fun updateCategory(category: Category, newName: String) {
        if (category.id.isNotEmpty()) {
            db.collection("categories").document(category.id).update("name", newName)
        }
    }

    /**
     * Deletne kategorii a přesune její úkoly do "default" - "Vše" kategorie
     */
    private fun deleteCategory(category: Category) {
        if (category.id.isNotEmpty()) {
            // 1. Delete samotné kategorie
            db.collection("categories").document(category.id).delete()
            
            // 2. Search všech úkolů v této kategorii a jejich transfer do "default"
            db.collection("tasks")
                .whereEqualTo("categoryId", category.id)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        db.collection("tasks").document(document.id).update("categoryId", "default")
                    }
                }
        }
    }

    private fun showAddCategoryDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_category, null)
        val editName = dialogView.findViewById<EditText>(R.id.editCategoryName)

        AlertDialog.Builder(requireContext())
            .setTitle("Přidat kategorii")
            .setView(dialogView)
            .setPositiveButton("Přidat") { _, _ ->
                val name = editName.text.toString()
                if (name.isNotEmpty()) {
                    addCategory(name)
                } else {
                    Toast.makeText(context, "Název nesmí být prázdný", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Zrušit", null)
            .show()
    }

    private fun showEditCategoryDialog(category: Category) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_category, null)
        val editName = dialogView.findViewById<EditText>(R.id.editCategoryName)
        editName.setText(category.name)

        AlertDialog.Builder(requireContext())
            .setTitle("Upravit kategorii")
            .setView(dialogView)
            .setPositiveButton("Uložit") { _, _ ->
                val name = editName.text.toString()
                if (name.isNotEmpty()) {
                    updateCategory(category, name)
                } else {
                    Toast.makeText(context, "Název nesmí být prázdný", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Zrušit", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}