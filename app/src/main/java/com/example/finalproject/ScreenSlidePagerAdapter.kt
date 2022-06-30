package com.example.finalproject

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

public class ScreenSlidePagerAdapter(manager: FragmentManager, lifecycle: Lifecycle, notes: ArrayList<Note>, private val onLongClickListener: OnItemLongClickListener? = null) : FragmentStateAdapter(manager, lifecycle) {

    public interface OnItemLongClickListener {
        fun onItemLongClicked(position: Int, fragment: ScreenSlidePageFragment): Boolean
    }

    public interface OnContentEditRequestedListener {
        fun onContentEditRequested(position: Int, old_text: String)
    }

    var onContentEditRequestedListener: OnContentEditRequestedListener? = null

    val _notes = notes
    var lastLongClicked = -1

    private fun getLastClicked(): Int {
        return lastLongClicked
    }

    override fun getItemCount(): Int = _notes.size

    override fun createFragment(position: Int): Fragment {
        val note = _notes[position]
        val bundle = Bundle().apply {
            putString("first_key", note.content)
        }
        val fragment = ScreenSlidePageFragment().apply {
            arguments = bundle
            onEditRequestedListener = object : ScreenSlidePageFragment.OnEditRequestedListener {
                override fun onEditRequested(old_text: String) {
                    onContentEditRequestedListener?.onContentEditRequested(getLastClicked(), old_text)
                }
            }
        }
        fragment.onLongClickListener = object : ScreenSlidePageFragment.OnLongClickListener {
            override fun onLongClicked(fragment: ScreenSlidePageFragment): Boolean {
                Log.d("list_menu", position.toString())
                lastLongClicked = position
                return onLongClickListener?.onItemLongClicked(position, fragment) ?: false
            }
        }
        return fragment
    }
}