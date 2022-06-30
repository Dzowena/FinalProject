package com.example.finalproject

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_page_note.*
import kotlinx.android.synthetic.main.fragment_page_note.view.*

class ScreenSlidePageFragment : Fragment() {

    public interface OnLongClickListener {
        fun onLongClicked(fragment: ScreenSlidePageFragment): Boolean
    }

    public interface OnEditRequestedListener {
        fun onEditRequested(old_text: String)
    }

    public var onLongClickListener: OnLongClickListener? = null
    var onEditRequestedListener: OnEditRequestedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_page_note, container, false)
        val text = requireArguments().getString("first_key")
        view.textview.text = text

        view.textview.setOnLongClickListener {
            return@setOnLongClickListener onLongClickListener?.onLongClicked(this@ScreenSlidePageFragment) ?: false
        }

        registerForContextMenu(view.textview)

        return view
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (v.id == R.id.textview) {
            activity?.menuInflater?.inflate(R.menu.menu_note_page, menu)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.edit) {
            onEditRequestedListener?.onEditRequested(textview.text.toString())
            return true
        }
        return false
    }

}