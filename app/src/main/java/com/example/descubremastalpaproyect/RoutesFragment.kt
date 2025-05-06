package com.example.descubremastalpaproyect

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.descubremastalpaproyect.databinding.FragmentRoutesBinding

class RoutesFragment : Fragment(R.layout.fragment_routes) {

    private lateinit var binding: FragmentRoutesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentRoutesBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding.tvRoutes.text = "Fragment Rutas"

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_routes, container, false)
    }

}