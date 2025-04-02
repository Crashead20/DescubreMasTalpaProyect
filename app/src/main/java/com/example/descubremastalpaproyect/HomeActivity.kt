package com.example.descubremastalpaproyect

import android.os.Bundle

//Importacion de recursos
import android.widget.*

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType {
    BASIC
}

class HomeActivity : AppCompatActivity() {

    //Declarar las variables en la clase
    private lateinit var emailTextView: TextView
    private lateinit var providerTextView: TextView
    private lateinit var logoutButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Inicializar las variables aqui
        emailTextView = findViewById(R.id.emailTextView)
        providerTextView = findViewById(R.id.providerTextView)
        logoutButton = findViewById(R.id.logoutButton)


        //Setup de home
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        setup(email ?: "", provider ?: "")
    }
    private fun setup(email: String, provider: String){
        title = "Inicio"
        emailTextView.text = email
        providerTextView.text = provider

        logoutButton.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }


    }

}
