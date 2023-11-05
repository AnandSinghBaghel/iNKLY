package `in`.anandsingh.samay

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import `in`.anandsingh.samay.db.AppDatabase
import `in`.anandsingh.samay.db.Note
import `in`.anandsingh.samay.db.NoteRepository
import `in`.anandsingh.samay.db.NoteViewModel
import `in`.anandsingh.samay.db.NoteViewModelFactory
import `in`.anandsingh.samay.views.ContributionsGraphView


class MainActivity : AppCompatActivity() {

    private val noteViewModel by viewModels<NoteViewModel> {
        NoteViewModelFactory(NoteRepository(AppDatabase.getDatabase(application).noteDao()))
    }
    private var searchQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar()
        setupRecyclerView()
        setupFloatingActionButton()
        setupSwipeHandler()
        observeViewModel()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val searchView = findViewById<SearchView>(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchQuery = newText
                observeViewModel()
                return false
            }
        })
    }

    private fun observeViewModel() {
        noteViewModel.notesCountPerDay.observe(this, Observer { data ->
            val contributionsGraphView =
                findViewById<ContributionsGraphView>(R.id.contributions_graph_view)
            val contributionsData = data.associate { it.date to it.count }
            contributionsGraphView.setData(contributionsData)
        })

        if (searchQuery.isNullOrEmpty()) {
            noteViewModel.allNotes.observe(this, Observer { notes ->
                updateRecyclerView(notes)
            })
        } else {
            noteViewModel.filterNotes(searchQuery ?: "")
            noteViewModel.filteredNotes.observe(this, Observer { filteredNotes ->
                updateRecyclerView(filteredNotes)
            })
        }
    }

    private fun updateRecyclerView(notes: List<Note>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        (recyclerView.adapter as NoteAdapter).setNotes(notes)
    }

    private fun setupRecyclerView() {
        val adapter = NoteAdapter()
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupFloatingActionButton() {
        val fab = findViewById<FloatingActionButton>(R.id.fab_add_note)
        fab.setOnClickListener {
            Intent(this, CreateNoteActivity::class.java).also { startActivity(it) }
        }
    }

    private fun setupSwipeHandler() {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false  // not interested in drag-and-drop
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val note = (recyclerView.adapter as NoteAdapter).getNoteAt(position)
                if (direction == ItemTouchHelper.LEFT) {
                    // Edit note on left swipe
                    Intent(this@MainActivity, CreateNoteActivity::class.java).apply {
                        putExtra(CreateNoteActivity.EXTRA_NOTE, note)
                    }.also { startActivity(it) }
                    (recyclerView.adapter as NoteAdapter).notifyItemChanged(position)  // Reset swiped item
                } else {
                    // Delete note on right swipe
                    noteViewModel.delete(note)
                    Toast.makeText(this@MainActivity, "Note deleted", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}


