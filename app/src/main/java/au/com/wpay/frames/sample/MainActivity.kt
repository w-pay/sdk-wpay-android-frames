package au.com.wpay.frames.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navController = findNavController()
        val navView = findViewById<NavigationView>(R.id.nav_view)

        drawerLayout = findViewById(R.id.drawer)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.singleCardCaptureItem -> navController.navigate(R.id.singleCardCapture)
                R.id.multiLineCardCaptureItem -> navController.navigate(R.id.multiLineCardCapture)
                else -> throw IllegalStateException("Unknown menu item id")
            }

            drawerLayout.close()

            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return when {
            drawerLayout.isOpen -> {
                drawerLayout.close()

                true
            }
            else -> {
                findNavController().navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
            }
        }
    }

    private fun findNavController(): NavController {
        val host = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment

        return host.navController
    }
}
