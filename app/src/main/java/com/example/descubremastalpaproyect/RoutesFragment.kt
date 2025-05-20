package com.example.descubremastalpaproyect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore

data class Ubicacion(
    val nombre: String = "",
    val descripcion: String = "",
    val latitud: String = "",
    val longitud: String = ""
)

object RutaSeleccionada {
    var puntos: List<Ubicacion> = listOf()
}

class FragmentRoutes : Fragment() {

    private lateinit var layoutLista: LinearLayout
    private val ubicaciones = mutableListOf<Ubicacion>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_routes, container, false)

        layoutLista = view.findViewById(R.id.listaUbicaciones)
        val btnSeguir = view.findViewById<Button>(R.id.btnSeguirRuta)
        val tituloRuta = view.findViewById<TextView>(R.id.tituloRuta)

        tituloRuta.text = "Ruta Ejemplo 1"

        btnSeguir.setOnClickListener {
            RutaSeleccionada.puntos = ubicaciones
            findNavController().navigate(R.id.page_map)
        }

        cargarUbicaciones()

        return view
    }

    private fun cargarUbicaciones() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Lugares").document("ruta_1").collection("puntos")
            .get()
            .addOnSuccessListener { result ->
                layoutLista.removeAllViews()
                ubicaciones.clear()

                for (doc in result) {
                    val ubicacion = doc.toObject(Ubicacion::class.java)
                    ubicaciones.add(ubicacion)

                    val nombreView = TextView(requireContext())
                    nombreView.text = "${ubicaciones.size}. ${ubicacion.nombre}"
                    nombreView.textSize = 16f
                    nombreView.setPadding(16, 16, 16, 16)
                    layoutLista.addView(nombreView)
                }
            }
    }
}

private fun MatchGroup?.addOnSuccessListener(function: Any) {}