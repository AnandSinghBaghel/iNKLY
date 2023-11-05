package `in`.anandsingh.samay.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    val allNotes: LiveData<List<Note>> = repository.allNotes
    val notesCountPerDay: LiveData<List<NoteCountPerDay>> = repository.notesCountPerDay
    val filteredNotes: LiveData<List<Note>> get() = repository.filteredNotes

    fun insert(note: Note) = viewModelScope.launch {
        repository.insert(note)
    }

    fun update(note: Note) = viewModelScope.launch {
        repository.update(note)
    }

    fun delete(note: Note) = viewModelScope.launch {
        repository.delete(note)
    }

    fun filterNotes(query: String) {
        viewModelScope.launch {
            repository.filterNotes(query)
        }
    }

}

