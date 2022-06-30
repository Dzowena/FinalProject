package com.example.finalproject

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class NoteButtonAdapter(private val dataSet: List<Note>, private val onLongClickListener: View.OnLongClickListener? = null) : RecyclerView.Adapter<NoteButtonAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_note_button, parent, false)

        return ViewHolder(view, onLongClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == dataSet.size) {
            //holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f)
            holder.textView.text = "+"
        } else {
            //holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            holder.textView.text = dataSet[position].title
        }
    }

    override fun getItemCount(): Int = dataSet.size + 1

    class ViewHolder(view: View, onLongClickListener: View.OnLongClickListener?) : RecyclerView.ViewHolder(view) {
        val cardView: CardView
        val textView: TextView

        init {
            cardView = view.findViewById(R.id.card_view)
            textView = view.findViewById(R.id.text)

            cardView.setOnLongClickListener(onLongClickListener)
        }
    }

}