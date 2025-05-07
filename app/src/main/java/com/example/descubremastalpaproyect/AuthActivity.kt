package com.example.descubremastalpaproyect

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Patterns

//Importación de recursos
import android.widget.*
import androidx.activity.compose.setContent

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class AuthActivity : AppCompatActivity() {

    object Global{
        var preferencias_compartidas="sharedPreferences"
    }

    //Declarar las variables en la clase
    private lateinit var signupButton: Button
    private lateinit var loginButton: Button
    private lateinit var googleButton: ImageButton
    private lateinit var email: EditText
    private lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)

        verificar_sesion_abierta()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Analytics Event
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integracion de Firebase completa")
        analytics.logEvent("InitScreen", bundle)

        //Inicializar las variables aqui
        signupButton = findViewById(R.id.signupButton)
        loginButton = findViewById(R.id.loginButton)
        googleButton = findViewById(R.id.googleButton)
        email = findViewById(R.id.editTextEmail)//email
        password = findViewById(R.id.passwordEditText)//password

        loginButton.setOnClickListener {
            if (password.text.toString() != "") {
                if (email.text.toString() != "" && Patterns.EMAIL_ADDRESS.matcher(email.text.toString())
                        .matches()
                ) {
                    login_firebase(email.text.toString(), password.text.toString())
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Ingrese un correo valido",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(applicationContext, "Ingrese una contraseña", Toast.LENGTH_LONG)
                    .show()
            }
        }

        signupButton.setOnClickListener {
            if (password.text.toString() != "") {
                if (email.text.toString() != "" && Patterns.EMAIL_ADDRESS.matcher(email.text.toString())
                        .matches()
                ) {
                    signup_firebase(email.text.toString(), password.text.toString())
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Ingrese un correo valido",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(applicationContext, "Ingrese una contraseña", Toast.LENGTH_LONG)
                    .show()
            }
        }

        googleButton.setOnClickListener {
            setContent{
                loginGoogle()
            }
        }
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun loginGoogle(){
        val context = LocalContext.current
        val coroutineScope:CoroutineScope = rememberCoroutineScope()
        val credentialManager = CredentialManager.create(context)

        val signInWithGoogleOption: GetSignInWithGoogleOption = GetSignInWithGoogleOption.Builder(getString(R.string.web_client))
            .setNonce("nonce")
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()

        coroutineScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context,
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) {
                Toast.makeText(context, "Error al obtener la credencial", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun handleSignIn(result: GetCredentialResponse) {
        val credential = result.credential

        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)

                        val credencial = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

                        FirebaseAuth.getInstance().signInWithCredential(credencial)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    var intent = Intent(applicationContext, HomeActivity::class.java)
                                    intent.putExtra("email",task.result?.user?.email)
                                    intent.putExtra("proveedor", "GOOGLE")
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK // Evitar volver atrás
                                    startActivity(intent)
                                    guardar_sesion(task.result?.user?.email.toString(),"GOOGLE" )

                                } else {
                                    Toast.makeText(applicationContext, "Error en la autentificacion con firebase.", Toast.LENGTH_LONG).show()
                                }
                            }

                    } catch (e: GoogleIdTokenParsingException) {
                        Toast.makeText(applicationContext, "Received an invalid google id token response: "+e, Toast.LENGTH_LONG).show()
                        Log.e("AuthActivity", "Error: Received an invalid google id token response", e)
                    }
                } else {
                    Toast.makeText(applicationContext, "Unexpected type of credential", Toast.LENGTH_LONG).show()
                    Log.e("AuthActivity", "Unexpected type of credential")
                }
            }
            else -> {
                Toast.makeText(applicationContext, "Unexpected type of credential", Toast.LENGTH_LONG).show()
                Log.e("AuthActivity", "Unexpected type of credential")
            }
        }
    }

    fun login_firebase(email: String, password: String){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    var intent = Intent(applicationContext, HomeActivity::class.java)
                    intent.putExtra("email",task.result?.user?.email)
                    intent.putExtra("proveedor", "BASIC")
                    startActivity(intent)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK // Evitar volver atrás
                    guardar_sesion(task.result?.user?.email.toString(),"BASIC" )

                } else {
                    Toast.makeText(applicationContext, "Usuario y contraseña no registrados aun", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun signup_firebase(email: String,password: String){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    var intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("email",task.result?.user?.email)
                    intent.putExtra("proveedor", "BASIC")
                    startActivity(intent)

                    guardar_sesion(task.result?.user?.email.toString(),"BASIC" )

                    Toast.makeText(applicationContext, "Usuario registrado exitosamente", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, "Contraseña corta/usuario existente", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun verificar_sesion_abierta(){
        var sesion_abierta: SharedPreferences=this.getSharedPreferences(Global.preferencias_compartidas,Context.MODE_PRIVATE)
        var email=sesion_abierta.getString("email",null)
        var proveedor=sesion_abierta.getString("proveedor",null)

        if(email!=null && proveedor!=null){
            var intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("email", email)
            intent.putExtra("proveedor", proveedor)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK // Evitar volver atrás
            startActivity(intent)
            finish()
        }
    }

    fun guardar_sesion(email: String, proveedor: String){
        var guardar_sesion: SharedPreferences.Editor=this.getSharedPreferences(Global.preferencias_compartidas,Context.MODE_PRIVATE).edit()
        guardar_sesion.putString("email", email)
        guardar_sesion.putString("proveedor", proveedor)
        guardar_sesion.apply()
        guardar_sesion.commit()
    }
}