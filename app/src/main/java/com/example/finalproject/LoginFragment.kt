package com.example.finalproject

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (auth.currentUser != null) {
            val action = LoginFragmentDirections.actionLoginFragmentToNotesFragment()
            findNavController().navigate(action)
            return
        }

        register_button.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegistrationFragment()
            findNavController().navigate(action)
        }

        reset_button.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToResetPasswordFragment()
            findNavController().navigate(action)
        }

        login_button.setOnClickListener {
            val email = mail_input.text.toString().trim()
            val password = password_input.text.toString().trim()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val action = LoginFragmentDirections.actionLoginFragmentToNotesFragment()
                        findNavController().navigate(action)
                    } else {
                        Toast.makeText(this@LoginFragment.context, "Login failed!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

}