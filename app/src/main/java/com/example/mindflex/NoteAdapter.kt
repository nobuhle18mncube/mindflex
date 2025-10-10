package com.example.mindflex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(
    private var items: List<Note>,
    private val onClick: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvNoteTitle)
        val content: TextView = itemView.findViewById(R.id.tvNoteContent)
        init {
            itemView.setOnClickListener { onClick(items[adapterPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val n = items[position]
        holder.title.text = n.title ?: "(No title)"
        holder.content.text = n.content ?: ""
    }

    override fun getItemCount(): Int = items.size

    fun setData(newItems: List<Note>) {
        items = newItems
        notifyDataSetChanged()
    }
}
