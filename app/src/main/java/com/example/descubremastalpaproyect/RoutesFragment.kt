package com.example.descubremastalpaproyect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.descubremastalpaproyect.databinding.FragmentRoutesBinding

class RoutesFragment : Fragment(R.layout.fragment_routes) {

    private var _binding: FragmentRoutesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoutesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Texto ejemplo, puedes quitarlo si no lo necesitas
        binding.tvRoutes.text = "Fragment Rutas"

        // Cuando el usuario toca el botón, se navega al MapFragment
        // con el argumento 'generarRuta = true'
        binding.btnSeguirRuta.setOnClickListener {
            val bundle = Bundle().apply {
                putBoolean("generarRuta", true)
            }

            findNavController().navigate(
                R.id.action_page_routes_to_page_map, // Asegúrate de que este ID esté bien en nav_graph.xml
                bundle
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
