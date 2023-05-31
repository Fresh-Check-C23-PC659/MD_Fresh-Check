package com.example.freshcheck.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.freshcheck.R
import com.example.freshcheck.databinding.ActivityHomeBinding
import com.example.freshcheck.ui.fragments.DetectionFragment
import com.example.freshcheck.ui.fragments.HistoryFragment
import com.example.freshcheck.ui.fragments.HomeFragment
import com.example.freshcheck.ui.fragments.ProfileFragment

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            bottomNavigation.setOnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.nav_home -> {
                        replaceFragment(HomeFragment())
                        true
                    }
                    R.id.nav_detection -> {
                        replaceFragment(DetectionFragment())
                        true
                    }
                    R.id.nav_history -> {
                        replaceFragment(HistoryFragment())
                        true
                    }
                    R.id.nav_profile -> {
                        replaceFragment(ProfileFragment())
                        true
                    }
                    else -> false
                }
            }
        }

        replaceFragment(HomeFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.home_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}