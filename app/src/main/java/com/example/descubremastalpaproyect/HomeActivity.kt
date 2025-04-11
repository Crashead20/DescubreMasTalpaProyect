package com.example.descubremastalpaproyect

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.credentials.ClearCredentialStateRequest
import android.os.Bundle

//Importacion de recursos
import android.widget.*
import androidx.activity.compose.setContent

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import com.example.descubremastalpaproyect.AuthActivity.Global
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

enum class ProviderType {
    BASIC, //correo y contraseña BASICO
    GOOGLE //logueado con google
}

class HomeActivity : AppCompatActivity() {

    //Declarar las variables en la clase
    private lateinit var email: TextView
    private lateinit var proveedor: TextView
    private lateinit var exitButton: Button


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
        email = findViewById(R.id.emailTextView)
        proveedor = findViewById(R.id.providerTextView)
        exitButton = findViewById(R.id.exitButton)

        var intent=getIntent()
        email.text="Correo: "+intent.getStringExtra("email")
        proveedor.text="Proveedor: "+intent.getStringExtra("proveedor")

        exitButton.setOnClickListener{
            var intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)

            setContent{
                borrar_sesion()
            }
        }
    }

    //FUNCIONES---------------------------------------
    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun borrar_sesion(){
        var borrar_sesion: SharedPreferences.Editor=this.getSharedPreferences(Global.preferencias_compartidas,
            MODE_PRIVATE
        ).edit()
        borrar_sesion.clear()
        borrar_sesion.apply()
        borrar_sesion.commit()

        Firebase.auth.signOut()

        val context = LocalContext.current
        val coroutineScope:CoroutineScope = rememberCoroutineScope()
        val credentialManager = CredentialManager.create(context)

        coroutineScope.launch {
            val clearRequest = androidx.credentials.ClearCredentialStateRequest(androidx.credentials.ClearCredentialStateRequest.TYPE_CLEAR_RESTORE_CREDENTIAL)
            credentialManager.clearCredentialState(clearRequest)
        }

    }

}

