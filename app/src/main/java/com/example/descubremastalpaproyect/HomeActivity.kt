package com.example.descubremastalpaproyect
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
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

        // Configurar navegación
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.BottomNavigationView, navController)

        // Configurar botones del custom toolbar
        val toolbar = findViewById<Toolbar>(R.id.topAppBar)
        val accountBtn = toolbar.findViewById<ImageButton>(R.id.account_button)
        val settingsBtn = toolbar.findViewById<ImageButton>(R.id.settings_button)

        accountBtn.setOnClickListener {
            navController.navigate(R.id.page_account)
        }
        settingsBtn.setOnClickListener {
            navController.navigate(R.id.page_settings)
        }
        // Edge-to-edge
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sysBars.left, sysBars.top, sysBars.right, 0)
            insets
        }
    }
}

