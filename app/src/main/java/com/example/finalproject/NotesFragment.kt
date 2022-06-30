package com.example.finalproject

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_notes.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class NotesFragment : Fragment(R.layout.fragment_notes) {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
    }

    interface OnItemClickListener {
        fun onItemClicked(position: Int, view: View)
    }

    fun RecyclerView.addOnItemClickListener(onClickListener: OnItemClickListener) {
        this.addOnChildAttachStateChangeListener(object: RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewDetachedFromWindow(view: View) {
                view.setOnClickListener(null)
            }

            override fun onChildViewAttachedToWindow(view: View) {
                view.setOnClickListener {
                    val holder = getChildViewHolder(view)
                    onClickListener.onItemClicked(holder.adapterPosition, view)
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = Firebase.database("https://final-project-ea054-default-rtdb.europe-west1.firebasedatabase.app/")
        val userRef = database.getReference(auth.currentUser?.uid.toString())

        userRef.child("notes").addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val notes = ArrayList<Note>()

                for (keySnapshot: DataSnapshot in snapshot.children) {
                    val key = keySnapshot.key
                    val note: Note? = keySnapshot.getValue<Note>()
                    if (note != null && key != null) {
                        note.key = key
                        notes.add(note)
                    }
                }

                val pagerAdapter = ScreenSlidePagerAdapter(childFragmentManager, lifecycle, notes)
                pager.adapter = pagerAdapter
                pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        recycler_view.smoothScrollToPosition(position)
                    }
                })

                val recyclerAdapter = NoteButtonAdapter(notes) {
                    false
                }
                recycler_view.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                recycler_view.adapter = recyclerAdapter
                recycler_view.addOnItemClickListener(object : OnItemClickListener {
                    override fun onItemClicked(position: Int, view: View) {
                        if (position == notes.size) {
                            val note = Note("n$position", position, "Note ${notes.size + 1}", "Note ${notes.size + 1}")
                            notes.add(note)
                            recyclerAdapter.notifyItemInserted(notes.size - 1)
                            pagerAdapter.notifyItemInserted(notes.size - 1)
                            Executors.newSingleThreadScheduledExecutor().schedule({
                                recycler_view.smoothScrollToPosition(notes.size)
                            }, 200, TimeUnit.MILLISECONDS)

                            val noteRef = userRef.child("notes").child("n$position")
                            noteRef.setValue(notes[position])
                        }
                        pager.currentItem = position
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("firebase", error.message)
                Log.e("firebase", error.details)
                Toast.makeText(this@NotesFragment.context, "Cancelled! " + error.message, Toast.LENGTH_LONG).show()
            }

        })

        /*.setOnClickListener {
            val action = NotesFragmentDirections.actionNotesFragmentToChangePasswordFragment()
            findNavController().navigate(action)
        }*/
    }

}