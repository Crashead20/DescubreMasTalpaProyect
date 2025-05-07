package com.example.descubremastalpaproyect

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class AccountFragment : Fragment() {

    private lateinit var emailTextView: TextView
    private lateinit var providerTextView: TextView
    private lateinit var logoutButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        emailTextView = view.findViewById(R.id.txtEmail)
        providerTextView = view.findViewById(R.id.txtProvider)
        logoutButton = view.findViewById(R.id.logoutButton)

        // Recuperar datos desde SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(
            AuthActivity.Global.preferencias_compartidas,
            Context.MODE_PRIVATE
        )

        val email = sharedPreferences.getString("email", "Email no disponible")
        val provider = sharedPreferences.getString("proveedor", "Proveedor no disponible")

        emailTextView.text = "Correo: $email"
        providerTextView.text = "Proveedor: $provider"

        logoutButton.setOnClickListener {
            cerrarSesion()
        }

        return view
    }

    private fun cerrarSesion() {
        // Borrar SharedPreferences
        sharedPreferences.edit().clear().apply()

        // Cerrar sesión en Firebase
        FirebaseAuth.getInstance().signOut()

        // Ir a la pantalla de autenticación
        val intent = Intent(activity, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}