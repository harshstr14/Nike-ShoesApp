package com.example.nike

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.nike.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var googleSignInManager: GoogleSignInManager ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        googleSignInManager = GoogleSignInManager.getInstance(this)
        val pref = getSharedPreferences("Pref_Name",MODE_PRIVATE)
        val isLoggedIn = pref.getBoolean("isLoggedIn",false)

        Handler(Looper.getMainLooper()).postDelayed({
            if (isLoggedIn) {
                val intent = Intent(this, Home::class.java)
                startActivity(intent)
                finish()
            } else if (googleSignInManager!!.isUserAlreadySignedIn) {
                val intent = Intent(this, Home::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, Screen1::class.java)
                startActivity(intent)
                finish()
            }
        },3000)

    }
}