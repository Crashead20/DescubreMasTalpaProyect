package com.example.descubremastalpaproyect

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog

class MapFragment : Fragment() {

    private lateinit var webView: WebView
    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(requireContext()) }
    private var bottomSheetDialog: BottomSheetDialog? = null

    @SuppressLint("SetJavaScriptEnabled", "MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        webView = view.findViewById(R.id.webView)

        // Configuración WebView
        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.domStorageEnabled = true
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = WebViewClient()

        // Añadir interfaz para comunicación JS <-> Android
        webView.addJavascriptInterface(JSInterface(), "Android")

        // Carga el archivo html del assets
        webView.loadUrl("file:///android_asset/map.html")

        // FUNCION PARA CARGAR RUTA 1
        webView.postDelayed({
            val puntosJS = RutaSeleccionada.puntos.joinToString(separator = ",") {
                "['${it.nombre}', ${it.latitud}, ${it.longitud}]"
            }
            val jsCode = "dibujarRuta([$puntosJS]);"
            webView.evaluateJavascript(jsCode, null)
        }, 2000) // Espera a que cargue el mapa y JS


        // Solicitar actualizaciones de ubicación
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 5000
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                // Envía la ubicación a JS
                webView.post {
                    webView.evaluateJavascript("setInitialLocation(${location.latitude}, ${location.longitude});", null)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        return view
    }

    inner class JSInterface {
        @JavascriptInterface
        fun showInfo(nombre: String, descripcion: String, imagenUrl: String) {
            activity?.runOnUiThread {
                showBottomSheet(nombre, descripcion, imagenUrl)
            }
        }
    }

    private fun showBottomSheet(nombre: String, descripcion: String, imagenUrl: String) {
        if (bottomSheetDialog == null) {
            bottomSheetDialog = BottomSheetDialog(requireContext())
        }
        val view = layoutInflater.inflate(R.layout.bottom_sheet_info, null)
        val nombreText = view.findViewById<TextView>(R.id.nombreText)
        val descripcionText = view.findViewById<TextView>(R.id.descripcionText)
        val imagenView = view.findViewById<ImageView>(R.id.imagenView)

        nombreText.text = nombre
        descripcionText.text = descripcion

        if (imagenUrl != "Sin URL" && imagenUrl.isNotBlank()) {
            Glide.with(this).load(imagenUrl).into(imagenView)
        } else {
            imagenView.setImageResource(R.drawable.baseline_refresh_24) // Imagen de placeholder local
        }

        bottomSheetDialog?.setContentView(view)
        bottomSheetDialog?.show()
    }
}