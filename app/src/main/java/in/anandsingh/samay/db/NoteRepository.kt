package `in`.anandsingh.samay.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NoteRepository(private val noteDao: NoteDao) {

    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()
    val notesCountPerDay: LiveData<List<NoteCountPerDay>> = noteDao.getNotesCountPerDay()
    private val _filteredNotes = MutableLiveData<List<Note>>()
    val filteredNotes: LiveData<List<Note>> get() = _filteredNotes

    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    suspend fun update(note: Note) {
        noteDao.update(note)
    }

    suspend fun delete(note: Note) {
        noteDao.delete(note)
    }

    suspend fun filterNotes(query: String) {
        withContext(Dispatchers.IO) {
            val filteredList = noteDao.getFilteredNotes("%$query%")
            _filteredNotes.postValue(filteredList)
        }
    }
}
