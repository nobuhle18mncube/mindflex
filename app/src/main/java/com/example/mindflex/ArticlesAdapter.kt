package com.example.mindflex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ArticlesAdapter(
    private var items: List<GNewsArticle> = emptyList(),
    private val onClick: (GNewsArticle) -> Unit
) : RecyclerView.Adapter<ArticlesAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvArticleTitle)
        val source: TextView = itemView.findViewById(R.id.tvArticleSource)
        val date: TextView = itemView.findViewById(R.id.tvArticleDate)
        init {
            itemView.setOnClickListener {
                val article = items.getOrNull(bindingAdapterPosition)
                if (article != null) onClick(article)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val a = items[position]
        holder.title.text = a.title ?: "(no title)"
        holder.source.text = a.source?.name ?: ""
        holder.date.text = a.publishedAt ?: ""
    }

    override fun getItemCount(): Int = items.size

    fun setArticles(list: List<GNewsArticle>) {
        items = list
        notifyDataSetChanged()
    }
}
