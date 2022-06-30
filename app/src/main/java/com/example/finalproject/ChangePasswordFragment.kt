package com.example.finalproject

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_change_password.*
import kotlinx.android.synthetic.main.fragment_registration.*


class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        change_button.setOnClickListener {
            val old_password = old_password_input.text.toString().trim()
            val new_password = new_password_input.text.toString().trim()
            val mail = auth.currentUser?.email.toString().trim()
            val credential = EmailAuthProvider
                .getCredential(mail, old_password)
            auth.currentUser?.reauthenticate(credential)?.addOnCompleteListener {
                if (it.isSuccessful) {
                    auth.currentUser?.updatePassword(new_password)
                        ?.addOnCompleteListener {
                            if (it.isSuccessful) {
                                findNavController().navigateUp()
                            } else {
                                Toast.makeText(this@ChangePasswordFragment.context, "password change failed!", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this@ChangePasswordFragment.context, "incorrect password!", Toast.LENGTH_SHORT).show()
                }
            }


            }

    }

}