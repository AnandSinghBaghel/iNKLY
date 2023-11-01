package `in`.anandsingh.samay

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import `in`.anandsingh.samay.db.AppDatabase
import `in`.anandsingh.samay.db.Note
import `in`.anandsingh.samay.db.NoteRepository
import `in`.anandsingh.samay.db.NoteViewModel
import `in`.anandsingh.samay.db.NoteViewModelFactory

class CreateNoteActivity : AppCompatActivity() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextNote: TextInputEditText
    private lateinit var textViewCharacterCount: TextView
    private lateinit var buttonSave: MaterialButton
    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        val noteDao = AppDatabase.getDatabase(application).noteDao()
        val repository = NoteRepository(noteDao)
        val viewModelFactory = NoteViewModelFactory(repository)
        noteViewModel = ViewModelProvider(this, viewModelFactory).get(NoteViewModel::class.java)
        editTextNote = findViewById(R.id.edit_text_note)
        textViewCharacterCount = findViewById(R.id.text_view_character_count)
        buttonSave = findViewById(R.id.button_save)
        editTextTitle = findViewById(R.id.edit_text_title)

        editTextNote.addTextChangedListener { text ->
            val characterCount = text?.length ?: 0
            textViewCharacterCount.text = "$characterCount/280"
        }

        buttonSave.setOnClickListener {
            saveNote()
        }
    }

    private fun saveNote() {
        val title = editTextTitle.text.toString()
        val noteText = editTextNote.text.toString()
        if (noteText.isNotEmpty()) {
            val note = Note(title = title, content = noteText)
            noteViewModel.insert(note)
            finish()
        } else {
            Toast.makeText(this, "Note cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }
}

