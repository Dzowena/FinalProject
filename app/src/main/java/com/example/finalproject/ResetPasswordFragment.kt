package com.example.finalproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.mail_input
import kotlinx.android.synthetic.main.fragment_login.reset_button
import kotlinx.android.synthetic.main.fragment_reset_password.*

class ResetPasswordFragment : Fragment(R.layout.fragment_reset_password) {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reset_button.setOnClickListener {
            val email = mail_input.text.toString().trim()

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this@ResetPasswordFragment.context, "Check you email", Toast.LENGTH_LONG).show()
                        findNavController().navigateUp()
                    } else {
                        Toast.makeText(this@ResetPasswordFragment.context, "Action failed!", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

}