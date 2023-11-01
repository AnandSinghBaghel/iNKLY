package `in`.anandsingh.samay

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import `in`.anandsingh.samay.db.Note
import io.noties.markwon.Markwon
import java.text.DateFormat
import java.util.Date

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private var notes = emptyList<Note>()

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.text_view_title)
//        val contentTextView: TextView = itemView.findViewById(R.id.text_view_content)
        val textViewDate: TextView = itemView.findViewById(R.id.text_view_date)
        val markdownView: TextView = itemView.findViewById(R.id.markdown_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = notes[position]
        holder.titleTextView.text = currentNote.title
        val markwon = Markwon.create(holder.itemView.context)
        markwon.setMarkdown(holder.markdownView, currentNote.content)
        holder.textViewDate.text = DateFormat.getDateTimeInstance().format(Date(currentNote.timestamp))
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    internal fun setNotes(notes: List<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }

    fun getNoteAt(position: Int): Note {
        return notes[position]
    }
}
