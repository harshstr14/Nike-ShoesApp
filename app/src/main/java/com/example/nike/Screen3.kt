package com.example.nike

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.nike.databinding.ActivityScreen3Binding

class Screen3 : AppCompatActivity() {
    private lateinit var binding: ActivityScreen3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityScreen3Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val animation = AnimationUtils.loadAnimation(this,R.anim.enter_from_right)
        binding.imageView.startAnimation(animation)

        binding.materialButton3.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
    }
}