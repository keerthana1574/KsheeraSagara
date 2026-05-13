package com.example.ksheerasagara

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.ksheerasagara.databinding.ActivityMainBinding
import com.example.ksheerasagara.firebase.FirebaseAuthManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var authManager: FirebaseAuthManager
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authManager = FirebaseAuthManager(this)

        // Check if user is logged in
        if (!authManager.isSignedIn()) {
            goToLoginActivity()
            return
        }

        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = "Ksheera Sagara"

        // Setup drawer
        drawerLayout = binding.drawerLayout
        toggle = ActionBarDrawerToggle(
            this, drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Setup bottom navigation with NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setupWithNavController(navController)

        // Setup navigation drawer logout
        val navigationView = findViewById<NavigationView>(R.id.navView)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    authManager.signOut()
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                    goToLoginActivity()
                    true
                }
                else -> false
            }
        }

        // Load user profile into drawer header
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val navigationView = findViewById<NavigationView>(R.id.navView)
        val headerView = navigationView.getHeaderView(0)
        val tvUserName = headerView.findViewById<TextView>(R.id.tvUserName)
        val tvUserEmail = headerView.findViewById<TextView>(R.id.tvUserEmail)

        // Get user name from Firestore
        authManager.getUserName { name ->
            tvUserName.text = name
        }
        // Set email
        tvUserEmail.text = authManager.currentUser?.email ?: "user@example.com"
    }

    private fun goToLoginActivity() {
        val intent = android.content.Intent(this, LoginActivity::class.java)
        intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp() || super.onSupportNavigateUp()
    }
}