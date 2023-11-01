package `in`.anandsingh.samay

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import `in`.anandsingh.samay.db.AppDatabase
import `in`.anandsingh.samay.db.NoteRepository
import `in`.anandsingh.samay.db.NoteViewModel
import `in`.anandsingh.samay.db.NoteViewModelFactory
import `in`.anandsingh.samay.views.ContributionsGraphView

class MainActivity : AppCompatActivity() {

    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val noteDao = AppDatabase.getDatabase(application).noteDao()
        val repository = NoteRepository(noteDao)
        val viewModelFactory = NoteViewModelFactory(repository)
        noteViewModel = ViewModelProvider(this, viewModelFactory).get(NoteViewModel::class.java)

        // Set up RecyclerView
        val adapter = NoteAdapter()
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Observe LiveData from ViewModel
        noteViewModel.allNotes.observe(this, Observer { notes ->
            // Update RecyclerView
            adapter.setNotes(notes)
        })

        noteViewModel.notesCountPerDay.observe(this, Observer { data ->
            val contributionsGraphView = findViewById<ContributionsGraphView>(R.id.contributions_graph_view)
            val contributionsData = data.associate { it.date to it.count }
            contributionsGraphView.setData(contributionsData)
        })

        // Set up FloatingActionButton
        val fab = findViewById<FloatingActionButton>(R.id.fab_add_note)
        fab.setOnClickListener {
            // Launch AddNoteActivity (yet to be created)
            val intent = Intent(this, CreateNoteActivity::class.java)
            startActivity(intent)
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false  // not interested in drag-and-drop
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val note = adapter.getNoteAt(position)
                noteViewModel.delete(note)
                Toast.makeText(this@MainActivity, "Note deleted", Toast.LENGTH_SHORT).show()
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(findViewById(R.id.recycler_view))  // assuming your RecyclerView has the ID "recycler_view"
    }
}

