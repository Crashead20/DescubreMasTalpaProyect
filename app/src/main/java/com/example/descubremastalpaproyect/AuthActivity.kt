package com.example.descubremastalpaproyect

import android.content.Intent
import android.os.Bundle

//Importacion de recursos
import android.widget.*

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity() {

    //Declarar las variables en la clase
    private lateinit var signupButton: Button
    private lateinit var loginButton: Button
    private lateinit var editTextEmail: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Analytics Event
        val analytics  = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integracion de Firebase completa")
        analytics.logEvent("InitScreen", bundle)

        //Inicializar las variables aqui
        signupButton = findViewById(R.id.signupButton)
        loginButton = findViewById(R.id.loginButton)
        editTextEmail = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.passwordEditText)

        //Setup de la aplicacion
        setup()
    }

    private fun setup(){
        title = "Autenticacion"

        signupButton.setOnClickListener{
            if (editTextEmail.text.isNotEmpty() && passwordEditText.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(editTextEmail.text.toString(),
                        passwordEditText.text.toString()).addOnCompleteListener{

                            if (it.isSuccessful){
                                showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                            }else{
                                showAlert()
                            }
                    }
            }
        }

        loginButton.setOnClickListener{
            if (editTextEmail.text.isNotEmpty() && passwordEditText.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(editTextEmail.text.toString(),
                        passwordEditText.text.toString()).addOnCompleteListener{

                        if (it.isSuccessful){
                            showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                        }else{
                            showAlert()
                        }
                    }
            }
        }

    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderType){
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email",email)
            putExtra("provider",provider.name)
        }
        startActivity(homeIntent)
    }
}