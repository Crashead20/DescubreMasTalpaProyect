package com.example.descubremastalpaproyect

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.descubremastalpaproyect.databinding.FragmentMapBinding
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetDialog

class MapFragment : Fragment() {

    private lateinit var webView: WebView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var bottomSheetDialog: BottomSheetDialog? = null

    @SuppressLint("SetJavaScriptEnabled", "MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        webView = view.findViewById(R.id.webView)

        // Configuración de WebView
        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.domStorageEnabled = true
        webView.webChromeClient = WebChromeClient()

        val generarRuta = arguments?.getBoolean("generarRuta", false) ?: false

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
                // Establecer si se debe generar la ruta
                webView.evaluateJavascript("setShouldGenerateRoute($generarRuta);", null)
            }
        }

        webView.addJavascriptInterface(JSInterface(), "Android")
        webView.loadUrl("file:///android_asset/map.html")

        // Configurar ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = 5000
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                webView.evaluateJavascript("setInitialLocation(${location.latitude}, ${location.longitude});", null)
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
        val view = layoutInflater.inflate(R.layout.bottom_sheet_info, null)
        val nombreText = view.findViewById<TextView>(R.id.nombreText)
        val descripcionText = view.findViewById<TextView>(R.id.descripcionText)
        val imagen = view.findViewById<ImageView>(R.id.imagenView)
        val boton = view.findViewById<Button>(R.id.btnAgregar)

        nombreText.text = nombre
        descripcionText.text = descripcion
        Glide.with(requireContext()).load(imagenUrl)
            .placeholder(R.drawable.baseline_refresh_24)
            .error(R.drawable.ic_map)
            .into(imagen)

        boton.setOnClickListener {
            Toast.makeText(requireContext(), "Agregar acción futura", Toast.LENGTH_SHORT).show()
        }

        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog?.setContentView(view)
        bottomSheetDialog?.show()
    }
}
