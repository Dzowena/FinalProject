package com.example.finalproject

import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_notes.*
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class NotesFragment : Fragment(R.layout.fragment_notes) {

    private lateinit var auth: FirebaseAuth
    private lateinit var userRef: DatabaseReference

    private val notes = ArrayList<Note>()
    private lateinit var pagerAdapter: ScreenSlidePagerAdapter
    private lateinit var recyclerAdapter: NoteButtonAdapter
    private var recyclerViewLongClickPosition = -1
    private var viewPagerLongClickPosition = -1

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
        userRef = database.getReference(auth.currentUser?.uid.toString())

        userRef.child("notes").addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                for (keySnapshot: DataSnapshot in snapshot.children) {
                    val key = keySnapshot.key
                    val note: Note? = keySnapshot.getValue<Note>()
                    if (note != null && key != null) {
                        note.key = key
                        notes.add(note)
                    }
                }

                val sorted = notes.sortedWith(compareBy { it.position })
                notes.clear()
                notes.addAll(sorted)

                pagerAdapter = ScreenSlidePagerAdapter(childFragmentManager, lifecycle, notes,
                    object : ScreenSlidePagerAdapter.OnItemLongClickListener {
                        override fun onItemLongClicked(
                            position: Int,
                            fragment: ScreenSlidePageFragment
                        ): Boolean {
                            recyclerViewLongClickPosition = -1
                            viewPagerLongClickPosition = position
                            return false
                        }
                    })
                //registerForContextMenu(pager)
                pager.adapter = pagerAdapter
                pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        recycler_view.smoothScrollToPosition(position + 1)
                    }
                })
                pagerAdapter.onContentEditRequestedListener = object : ScreenSlidePagerAdapter.OnContentEditRequestedListener {
                    override fun onContentEditRequested(position: Int, old_text: String) {
                        val dialog = EditTextDialogFragment()
                        val bundle = Bundle().apply {
                            putString("text", notes[position].content)
                        }
                        dialog.arguments = bundle
                        dialog.onDialogResultListener = object : EditTextDialogFragment.OnDialogResultListener {
                            override fun onDialogResult(text: String) {
                                notes[position].content = text
                                pagerAdapter.notifyDataSetChanged()
                                pager.invalidate()
                                pager.adapter = pagerAdapter
                                pager.currentItem = position
                                val noteRef = userRef.child("notes")
                                    .child(notes[position].key)
                                noteRef.setValue(notes[position])
                            }
                        }
                        dialog.show(activity?.supportFragmentManager!!, "editTextDialog")
                    }
                }

                registerForContextMenu(recycler_view)
                recyclerAdapter = NoteButtonAdapter(notes, object : NoteButtonAdapter.OnItemLongClickListener {
                    override fun onItemLongClicked(
                        position: Int,
                        view: NoteButtonAdapter.ViewHolder
                    ): Boolean {
                        recyclerViewLongClickPosition = position
                        viewPagerLongClickPosition = -1
                        return false
                    }
                })
                recycler_view.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                recycler_view.adapter = recyclerAdapter
                recycler_view.addOnItemClickListener(object : OnItemClickListener {
                    override fun onItemClicked(pos: Int, view: View) {
                        val position = if (pos == 0) {
                            val action = NotesFragmentDirections.actionNotesFragmentToSettingsFragment()
                            findNavController().navigate(action)

                            pos
                        } else {
                            pos - 1
                        }

                        if (position == notes.size) {
                            var uuid = ""
                            while (true) {
                                uuid = UUID.randomUUID().toString()
                                if (!notes.any { note -> note.key == uuid }) {
                                    break;
                                }
                            }

                            val note = Note(uuid, position, "Note ${notes.size + 1}", "Note ${notes.size + 1}")
                            notes.add(note)
                            recyclerAdapter.notifyItemInserted(notes.size)
                            pagerAdapter.notifyItemInserted(notes.size - 1)
                            Executors.newSingleThreadScheduledExecutor().schedule({
                                recycler_view.smoothScrollToPosition(notes.size)
                            }, 200, TimeUnit.MILLISECONDS)

                            val noteRef = userRef.child("notes").child(uuid)
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
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (v.id == R.id.recycler_view) {
            activity?.menuInflater?.inflate(R.menu.menu_note_button_list, menu)
        }/* else if (v.id == R.id.pager) {
            activity?.menuInflater?.inflate(R.menu.menu_note_page, menu)
        }*/
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.left -> {
                if (recyclerViewLongClickPosition > 1 && recyclerViewLongClickPosition != notes.size + 1) {
                    Collections.swap(notes, recyclerViewLongClickPosition - 2, recyclerViewLongClickPosition - 1)

                    recyclerAdapter.notifyItemRangeChanged(recyclerViewLongClickPosition - 1, 2)
                    pagerAdapter.notifyDataSetChanged()
                    pager.invalidate()
                    pager.adapter = pagerAdapter

                    notes[recyclerViewLongClickPosition - 2].position = recyclerViewLongClickPosition - 2
                    notes[recyclerViewLongClickPosition - 1].position = recyclerViewLongClickPosition - 1

                    var noteRef = userRef.child("notes").child(notes[recyclerViewLongClickPosition - 2].key)
                    noteRef.setValue(notes[recyclerViewLongClickPosition - 2])
                    noteRef = userRef.child("notes").child(notes[recyclerViewLongClickPosition - 1].key)
                    noteRef.setValue(notes[recyclerViewLongClickPosition - 1])
                }
            }
            R.id.right -> {
                if (recyclerViewLongClickPosition < notes.size && recyclerViewLongClickPosition != 0) {
                    Collections.swap(notes, recyclerViewLongClickPosition - 1, recyclerViewLongClickPosition)

                    recyclerAdapter.notifyItemRangeChanged(recyclerViewLongClickPosition, 2)
                    pagerAdapter.notifyDataSetChanged()
                    pager.invalidate()
                    pager.adapter = pagerAdapter

                    notes[recyclerViewLongClickPosition - 1].position = recyclerViewLongClickPosition - 1
                    notes[recyclerViewLongClickPosition].position = recyclerViewLongClickPosition

                    var noteRef = userRef.child("notes").child(notes[recyclerViewLongClickPosition - 1].key)
                    noteRef.setValue(notes[recyclerViewLongClickPosition - 1])
                    noteRef = userRef.child("notes").child(notes[recyclerViewLongClickPosition].key)
                    noteRef.setValue(notes[recyclerViewLongClickPosition])
                }
            }
            R.id.edit -> {
                if (activity?.supportFragmentManager != null) {
                    if (recyclerViewLongClickPosition == -1) return false

                    val dialog = EditTextDialogFragment()
                    val bundle = Bundle().apply {
                        putString("text", notes[recyclerViewLongClickPosition - 1].title)
                    }
                    dialog.arguments = bundle
                    dialog.onDialogResultListener = object : EditTextDialogFragment.OnDialogResultListener {
                        override fun onDialogResult(text: String) {
                            notes[recyclerViewLongClickPosition - 1].title = text
                            recyclerAdapter.notifyDataSetChanged()
                            val noteRef = userRef.child("notes")
                                .child(notes[recyclerViewLongClickPosition - 1].key)
                            noteRef.setValue(notes[recyclerViewLongClickPosition - 1])
                        }
                    }
                    dialog.show(activity?.supportFragmentManager!!, "editTextDialog")
                }
            }
            R.id.delete -> {
                val noteRef = userRef.child("notes")
                    .child(notes[recyclerViewLongClickPosition - 1].key)
                noteRef.removeValue()

                notes.removeAt(recyclerViewLongClickPosition - 1)
                recyclerAdapter.notifyDataSetChanged()
                pagerAdapter.notifyDataSetChanged()
                pager.invalidate()
                pager.adapter = pagerAdapter
            }
            else -> {
                return super.onContextItemSelected(item)
            }
        }

        return true
    }

}