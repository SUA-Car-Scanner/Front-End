package com.example.carscanner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
//import com.google.firebase.FirebaseApp
//import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    private val notificationFragment = NotificationFragment()
    private val drivingFragment = DrivingFragment()  // 기본 페이지
    private val settingsFragment = SettingsFragment()
    private val monitoringFragment = MonitoringFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //FirebaseApp.initializeApp(this)
        supportActionBar?.hide()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // 기본 페이지: 운전 기록 페이지 (DrivingFragment)
        loadFragment(monitoringFragment)
        bottomNavigationView.selectedItemId = R.id.navigation_monitoring
        bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.navigation_notifications -> {
                    loadFragment(notificationFragment)
                    true
                }
                R.id.navigation_monitoring -> {
                    loadFragment(monitoringFragment)
                    true
                }
                R.id.navigation_driving -> {
                    loadFragment(drivingFragment)
                    true
                }
                R.id.navigation_settings -> {
                    loadFragment(settingsFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
