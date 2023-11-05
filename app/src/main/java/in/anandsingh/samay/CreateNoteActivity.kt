package `in`.anandsingh.samay

import android.os.Bundle
import android.widget.ImageButton
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

    private lateinit var editTextNote: TextInputEditText
    private lateinit var textViewCharacterCount: TextView
    private lateinit var buttonSave: MaterialButton
    private lateinit var noteViewModel: NoteViewModel
    private var noteToEdit: Note? = null
    private var listNumber = 1

    companion object {
        const val EXTRA_NOTE = "in.anandsingh.samay.EXTRA_NOTE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)
        initializeViewModel()
        initializeUIComponents()
        setupListeners()
        handleIntentExtras()
    }

    private fun initializeViewModel() {
        val noteDao = AppDatabase.getDatabase(application).noteDao()
        val repository = NoteRepository(noteDao)
        val viewModelFactory = NoteViewModelFactory(repository)
        noteViewModel = ViewModelProvider(this, viewModelFactory).get(NoteViewModel::class.java)
    }

    private fun initializeUIComponents() {
        editTextNote = findViewById(R.id.edit_text_note)
        textViewCharacterCount = findViewById(R.id.text_view_character_count)
        buttonSave = findViewById(R.id.button_save)
    }

    private fun setupListeners() {
        editTextNote.addTextChangedListener { text ->
            val characterCount = text?.length ?: 0
            textViewCharacterCount.text = "$characterCount/280"
        }

        buttonSave.setOnClickListener {
            saveNote()
        }

        findViewById<ImageButton>(R.id.button_bold).setOnClickListener {
            applyBold()
        }

        findViewById<ImageButton>(R.id.button_italic).setOnClickListener {
            applyItalic()
        }

        findViewById<ImageButton>(R.id.button_underline).setOnClickListener {
            applyUnderline()
        }

        findViewById<ImageButton>(R.id.button_strike).setOnClickListener {
            applyStrikethrough()
        }

        findViewById<ImageButton>(R.id.button_bullet_list).setOnClickListener {
            applyBulletPoint()
        }

        findViewById<ImageButton>(R.id.button_numbered_list).setOnClickListener {
            applyNumberedList()
        }
    }

    private fun handleIntentExtras() {
        noteToEdit = intent.getParcelableExtra(EXTRA_NOTE)
        noteToEdit?.let {
            editTextNote.setText(it.content)
        }
    }

    private fun applyNumberedList() {
        val cursorPosition = editTextNote.selectionStart
        val text = editTextNote.text?.insert(cursorPosition, "$listNumber. ")
        editTextNote.setText(text)
        editTextNote.setSelection(cursorPosition + (listNumber.toString().length) + 2)  // place the cursor after the number and period
        listNumber++  // increment the list number for the next item
    }

    private fun applyBulletPoint() {
        val cursorPosition = editTextNote.selectionStart
        val text = editTextNote.text?.insert(cursorPosition, "- ")
        editTextNote.setText(text)
        editTextNote.setSelection(cursorPosition + 2)  // place the cursor after the bullet point
    }

    private fun applyBold() {
        val startSelection = editTextNote.selectionStart
        val endSelection = editTextNote.selectionEnd

        val selectedText = editTextNote.text?.subSequence(startSelection, endSelection).toString()
        val boldText = "**${selectedText}**"

        editTextNote.text?.replace(startSelection, endSelection, boldText)
    }

    private fun applyItalic() {
        val startSelection = editTextNote.selectionStart
        val endSelection = editTextNote.selectionEnd

        val selectedText = editTextNote.text?.subSequence(startSelection, endSelection).toString()
        val italicText = "_${selectedText}_"

        editTextNote.text?.replace(startSelection, endSelection, italicText)
    }

    private fun applyStrikethrough() {
        val startSelection = editTextNote.selectionStart
        val endSelection = editTextNote.selectionEnd

        val selectedText = editTextNote.text?.subSequence(startSelection, endSelection).toString()
        val strikethroughText = "~~${selectedText}~~"

        editTextNote.text?.replace(startSelection, endSelection, strikethroughText)
    }

    private fun applyUnderline() {
        val startSelection = editTextNote.selectionStart
        val endSelection = editTextNote.selectionEnd

        val selectedText = editTextNote.text?.subSequence(startSelection, endSelection).toString()
        val underlinedText = "<u>${selectedText}</u>"

        editTextNote.text?.replace(startSelection, endSelection, underlinedText)
    }


    private fun saveNote() {
        val noteText = editTextNote.text.toString()
        if (noteText.isNotEmpty()) {
            if (noteToEdit != null) {
                val updatedNote = Note(
                    id = noteToEdit!!.id,
                    title = "",
                    content = noteText
                )
                noteViewModel.update(updatedNote)
            } else {
                val newNote = Note(title = "", content = noteText)
                noteViewModel.insert(newNote)
            }
            finish()
        } else {
            Toast.makeText(this, "Note cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }
}

