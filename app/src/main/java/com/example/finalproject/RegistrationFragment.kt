package com.example.finalproject

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_registration.*

class RegistrationFragment : Fragment(R.layout.fragment_registration) {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        register_button.setOnClickListener {
            val email = mail_input.text.toString().trim()
            val password = password_input.text.toString().trim()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        findNavController().navigateUp()
                    } else {
                        Toast.makeText(this@RegistrationFragment.context, "Registration failed!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

}