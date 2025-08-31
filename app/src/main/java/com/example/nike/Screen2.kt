package com.example.nike

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.nike.databinding.ActivityScreen2Binding

class Screen2 : AppCompatActivity() {
    private lateinit var binding: ActivityScreen2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityScreen2Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val animation = AnimationUtils.loadAnimation(this,R.anim.enter_from_right)
        binding.imageView.startAnimation(animation)
        binding.imageView2.startAnimation(animation)
        
        binding.materialButton2.setOnClickListener {
            val intent = Intent(this, Screen3::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
    }
}