package com.example.finalproject

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        change_password_button.setOnClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToChangePasswordFragment()
            findNavController().navigate(action)
        }

        logout_button.setOnClickListener {
            auth.signOut()

            val action = SettingsFragmentDirections.actionSettingsFragmentToLoginFragment2()
            findNavController().navigate(action)
        }
    }

}