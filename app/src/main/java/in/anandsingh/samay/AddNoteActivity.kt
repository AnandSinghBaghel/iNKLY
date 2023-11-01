package `in`.anandsingh.samay

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import `in`.anandsingh.samay.databinding.ActivityAddNoteBinding
import `in`.anandsingh.samay.db.AppDatabase
import `in`.anandsingh.samay.db.Note
import `in`.anandsingh.samay.db.NoteRepository
import `in`.anandsingh.samay.db.NoteViewModel
import `in`.anandsingh.samay.db.NoteViewModelFactory

class AddNoteActivity : AppCompatActivity() {

    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        val noteDao = AppDatabase.getDatabase(application).noteDao()
        val repository = NoteRepository(noteDao)
        val viewModelFactory = NoteViewModelFactory(repository)
        noteViewModel = ViewModelProvider(this, viewModelFactory).get(NoteViewModel::class.java)

        val buttonSave = findViewById<Button>(R.id.button_save)
        val editTextTitle = findViewById<EditText>(R.id.edit_text_title)
        val editTextContent = findViewById<EditText>(R.id.edit_text_content)

        buttonSave.setOnClickListener {
            val title = editTextTitle.text.toString()
            val content = editTextContent.text.toString()
            val note = Note(title = title, content = content)
            noteViewModel.insert(note)
            finish()  // Close this activity and return to MainActivity
        }
    }
}
