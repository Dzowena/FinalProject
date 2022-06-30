package com.example.finalproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class NoteButtonAdapter(private val dataSet: List<Note>, private val onLongClickListener: OnItemLongClickListener? = null) : RecyclerView.Adapter<NoteButtonAdapter.ViewHolder>() {

    public interface OnItemLongClickListener {
        fun onItemLongClicked(position: Int, view: ViewHolder): Boolean
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listitem_note_button, parent, false)

        return ViewHolder(view, onLongClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) {
            holder.textView.text = "Settings"
        } else if (position == dataSet.size + 1) {
            //holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f)
            holder.textView.text = "+"
        } else {
            //holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            holder.textView.text = dataSet[position - 1].title
        }
    }

    override fun getItemCount(): Int = dataSet.size + 2

    class ViewHolder(view: View, val onLongClickListener: OnItemLongClickListener?) : RecyclerView.ViewHolder(view) {
        val cardView: CardView
        val textView: TextView

        init {
            cardView = view.findViewById(R.id.card_view)
            textView = view.findViewById(R.id.text)

            cardView.setOnLongClickListener {
                if (onLongClickListener != null) {
                    return@setOnLongClickListener onLongClickListener.onItemLongClicked(adapterPosition, this@ViewHolder)
                }
                else return@setOnLongClickListener false
            }
        }
    }

}