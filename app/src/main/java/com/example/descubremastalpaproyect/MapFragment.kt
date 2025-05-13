package com.example.descubremastalpaproyect

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.descubremastalpaproyect.databinding.FragmentMapBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val apiKey = "5b3ce3597851110001cf62488cc3dafc552544bc82fb9b54ab206789"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val webView = binding.webView // Asegúrate que en el layout tienes un WebView con id "webView"
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        webView.addJavascriptInterface(JSBridge(webView), "Android")
        webView.loadUrl("file:///android_asset/map.html")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class JSBridge(private val webView: WebView) {
        @JavascriptInterface
        fun sendCoordinates(coordsJson: String) {
            try {
                val coordsArray = coordsJson
                    .replace("[[", "")
                    .replace("]]", "")
                    .split("],[")

                if (coordsArray.size < 2) return

                val coordinates = coordsArray.map {
                    val parts = it.split(",")
                    listOf(parts[1].toDouble(), parts[0].toDouble()) // lng, lat
                }

                val requestBody = """
                    {
                        "coordinates": ${coordinates}
                    }
                """.trimIndent()

                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://api.openrouteservice.org/v2/directions/driving-car/geojson")
                    .addHeader("Authorization", apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body?.string()?.replace("\n", "")?.replace("'", "\\'")
                        webView.post {
                            webView.evaluateJavascript("drawRoute('$body')", null)
                        }
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
