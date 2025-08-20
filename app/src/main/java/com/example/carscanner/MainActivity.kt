package com.example.carscanner

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
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

    private lateinit var bottomNavigationView: BottomNavigationView
    private  lateinit var toolbarView: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //FirebaseApp.initializeApp(this)
        supportActionBar?.hide()

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        toolbarView = findViewById<Toolbar>(R.id.toolbarView)
        setSupportActionBar(toolbarView)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayUseLogoEnabled(false)

        val toolbarViewLayoutParams= toolbarView.layoutParams as AppBarLayout.LayoutParams
        toolbarViewLayoutParams.scrollFlags =
            AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
        toolbarView.layoutParams = toolbarViewLayoutParams
        toolbarView.bringToFront()
        toolbarView.isClickable = true

        ViewCompat.setElevation(toolbarView, 0f)


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

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.findFragmentById(R.id.fragment_container) is MonitoringFragment)
                    finish()
                else
                    loadFragment(monitoringFragment)
            }
        })
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        updateUI(fragment)
    }
    private fun updateUI(fragment: Fragment) {
        val isSettings = fragment is SettingsFragment
        toolbarView.visibility = if (isSettings) View.GONE else View.VISIBLE

        if(isSettings) {
            bottomNavigationView.menu.setGroupCheckable(0, true, false)
            for (i in 0 until bottomNavigationView.menu.size()){
                bottomNavigationView.menu.getItem(i).isChecked = false
            }
        } else {
            bottomNavigationView.menu.setGroupCheckable(0, true, true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        android.util.Log.d("MENU", "clicked id=${resources.getResourceEntryName(item.itemId)}")

        return when (item.itemId) {
            R.id.navigation_settings -> {
                loadFragment(settingsFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
