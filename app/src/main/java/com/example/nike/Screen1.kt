package com.example.nike

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.nike.databinding.ActivityScreen1Binding

class Screen1 : AppCompatActivity() {
    private lateinit var binding: ActivityScreen1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityScreen1Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val imageView = binding.imageView
        val animation = AnimationUtils.loadAnimation(this,R.anim.enter_from_right)
        imageView.startAnimation(animation)

        binding.materialButton.setOnClickListener {
            val intent = Intent(this, Screen2::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
    }

}