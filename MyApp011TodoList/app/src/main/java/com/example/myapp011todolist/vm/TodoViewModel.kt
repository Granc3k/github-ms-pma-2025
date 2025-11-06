package com.example.myapp011todolist.vm

import android.app.Application
import androidx.lifecycle.*
import com.example.myapp011todolist.data.Todo
import com.example.myapp011todolist.data.TodoStore
import kotlinx.coroutines.launch

class TodoViewModel(app: Application) : AndroidViewModel(app) {
    private val store = TodoStore(app)
    private val _items = MutableLiveData<List<Todo>>(emptyList())
    val items: LiveData<List<Todo>> = _items

    init {
        viewModelScope.launch { _items.value = store.load() }
    }

    private fun updateAndPersist(update: (List<Todo>) -> List<Todo>) {
        val newList = update(_items.value ?: emptyList())
        _items.value = newList
        viewModelScope.launch { store.save(newList) }
    }

    fun add(title: String) = updateAndPersist { it + Todo(title = title.trim()) }

    fun addWith(title: String, description: String?, imageUri: String?) = updateAndPersist {
        it + Todo(title = title.trim(), description = description?.trim().takeUnless { s -> s.isNullOrEmpty() }, imageUri = imageUri)
    }

    fun edit(id: String, title: String) = updateAndPersist {
        it.map { t -> if (t.id == id) t.copy(title = title.trim()) else t }
    }

    fun editWith(id: String, title: String, description: String?, imageUri: String?) = updateAndPersist {
        it.map { t ->
            if (t.id == id) {
                t.copy(
                    title = title.trim(),
                    description = description?.trim().takeUnless { s -> s.isNullOrEmpty() },
                    imageUri = imageUri
                )
            } else t
        }
    }

    fun toggle(id: String, checked: Boolean) = updateAndPersist {
        it.map { t -> if (t.id == id) t.copy(done = checked) else t }
    }

    fun remove(id: String) = updateAndPersist { it.filterNot { t -> t.id == id } }

    fun clearDone() = updateAndPersist { it.filterNot { t -> t.done } }
}
