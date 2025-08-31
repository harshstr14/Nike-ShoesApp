package com.example.nike

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import com.example.nike.databinding.ActivityRecoveryPasswordBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class RecoveryPassword : AppCompatActivity() {
    private lateinit var binding: ActivityRecoveryPasswordBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var emailField: AppCompatEditText
    private lateinit var continueBtn: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRecoveryPasswordBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.backArrowImage.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }
        binding.backArrowbtn.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        auth = FirebaseAuth.getInstance()
        emailField = findViewById(R.id.emailEditText)
        continueBtn = findViewById(R.id.continueBtn)

        continueBtn.setOnClickListener {
            val email = emailField.text.toString().trim()
            if (email.isEmpty()) {
                emailField.error = "Email Required"
                emailField.requestFocus()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Recovery Password","Password reset email sent")
                    Toast.makeText(this,"Password reset email sent", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("Recovery Password","Failed to sent: ${task.exception}")
                    Toast.makeText(this,"${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}