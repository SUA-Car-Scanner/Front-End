package com.example.carscanner

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
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
        val toolbarView = findViewById<Toolbar>(R.id.toolbarView)
        setSupportActionBar(toolbarView)

        val toolbarViewLayoutParams= toolbarView.layoutParams as AppBarLayout.LayoutParams
        toolbarViewLayoutParams.scrollFlags =
            AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
        toolbarView.layoutParams = toolbarViewLayoutParams


        loadFragment(monitoringFragment) // 기본 페이지 설정(실시간 모니터링 페이지)

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
                /*

                */

                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_settings -> {
                loadFragment(settingsFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
