package com.example.nike

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.nike.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class SignIn : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    private var googleSignInManager: GoogleSignInManager ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignInBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        googleSignInManager = GoogleSignInManager.getInstance(this)
        googleSignInManager?.setupGoogleSignInOptions()

        binding.googleSignInBtn.setOnClickListener {
            googleSignInManager!!.signIn()
        }


        binding.backArrowImage.setOnClickListener {
            val intent = Intent(this, Screen3::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }
        binding.backArrowbtn.setOnClickListener {
            val intent = Intent(this, Screen3::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        binding.recoverytxt.setOnClickListener {
            val intent = Intent(this, RecoveryPassword::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }

        binding.signInBtn.setOnClickListener {
            val mail = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (mail.isNotEmpty() && password.isNotEmpty()) {
                readData(mail,password)
            } else {
                Toast.makeText(this,"Enter Email and Password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signuptxt.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
    }

    private fun readData(mail: String,password: String) {
        val pref = getSharedPreferences("Pref_Name",MODE_PRIVATE)
        auth = FirebaseAuth.getInstance()

        Log.d("DEBUG", "Email: $mail, Password: $password")

        auth.signInWithEmailAndPassword(mail,password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                pref.edit { putBoolean("isLoggedIn",true) }
                val intent = Intent(this, Home::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
                finish()
            } else {
                try {
                    throw task.exception!!
                } catch (e: FirebaseAuthInvalidUserException) {
                    // Email not registered
                    Toast.makeText(this, "This email is not registered.", Toast.LENGTH_SHORT).show()
                    Log.e("Auth", "Email : ${e.message}")
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    // Wrong password
                    Toast.makeText(this, "Incorrect password.", Toast.LENGTH_SHORT).show()
                    Log.e("Auth", "Password : ${e.message}")
                } catch (e: Exception) {
                    // Other errors
                    Toast.makeText(this, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("Auth", "Other : ${e.message}")
                }
            }
        }.addOnFailureListener { e ->
            Log.e("Auth", "FirebaseAuth error: ${e.message}", e)
            Toast.makeText(this, "Log-In failed. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GoogleSignInManager.GOOGLE_SIGN_IN) {
            googleSignInManager?.handleSignInResult(data)
        }
    }
}