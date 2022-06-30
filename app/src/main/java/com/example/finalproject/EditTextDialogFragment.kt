package com.example.finalproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialogfragment_edit_text.view.*

class EditTextDialogFragment : DialogFragment() {
    interface OnDialogResultListener {
        fun onDialogResult(text: String)
    }

    public var onDialogResultListener: OnDialogResultListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialogfragment_edit_text, container, false)

        val text = requireArguments().getString("text")
        view.edit_text.setText(text)
        view.edit_text.hint = text

        view.cancel_btn.setOnClickListener {
            dismiss()
        }

        view.ok_btn.setOnClickListener {
            val newText = view.edit_text.text.toString()
            onDialogResultListener?.onDialogResult(newText)
            dismiss()
        }

        view.edit_text.requestFocus()
        view.edit_text.performClick()

        return view
    }
}