package com.example.descubremastalpaproyect

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.descubremastalpaproyect.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        // Obtener NavController del NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Configurar BottomNavigationView con NavController
        NavigationUI.setupWithNavController(binding.BottomNavigationView, navController)

        // Configurar ícono de cuenta (navegaciónIcon)
        binding.topAppBar.setNavigationOnClickListener {
            navController.navigate(R.id.page_account)
        }

        // Configurar ítem de configuración en el menú superior
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.icon_settings -> {
                    navController.navigate(R.id.page_settings)
                    true
                }
                else -> false
            }
        }

        // Soporte para edge-to-edge
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sysBars.left, sysBars.top, sysBars.right, 0)
            insets
        }
    }
}

