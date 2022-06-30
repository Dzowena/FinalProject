package com.example.finalproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

public class ScreenSlidePagerAdapter(manager: FragmentManager, lifecycle: Lifecycle, notes: ArrayList<Note>) : FragmentStateAdapter(manager, lifecycle) {
    val _notes = notes

    override fun getItemCount(): Int = _notes.size

    override fun createFragment(position: Int): Fragment {
        val note = _notes[position]
        val bundle = Bundle().apply {
            putString("first_key", note.content)
        }
        val fragment = ScreenSlidePageFragment().apply {
            arguments = bundle
        }
        return fragment
    }
}