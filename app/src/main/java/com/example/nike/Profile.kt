package com.example.nike

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.nike.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class Profile : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private var auth = FirebaseAuth.getInstance()
    private lateinit var database: DatabaseReference
    private lateinit var profileImageVIew: CircleImageView
    private lateinit var cameraIcon: ImageButton
    private var selectedImageUri: Uri ?= null
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                profileImageVIew.setImageURI(it)
                uploadToCloudinary(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityProfileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).isAppearanceLightNavigationBars = false

        handleBottomNavPosition()

        setStatusBarIconsTheme(this)

        profileImageVIew = binding.profileImage
        cameraIcon = binding.imageButton

        cameraIcon.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        loadProfileImage()

        binding.backArrowImage.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }
        binding.backArrowbtn.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        updateDetails()

        binding.updateProfileBtn.setOnClickListener {
            updateProfile()
        }

    }
    private fun uploadToCloudinary(imageUri: Uri) {
        Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show()

        MediaManager.get().upload(imageUri).option("folder","profile_pics")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) { }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) { }

                override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                    val imageUrl = resultData?.get("secure_url").toString()
                    saveImageUrlToDatabase(imageUrl)
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    Toast.makeText(this@Profile, "Upload failed", Toast.LENGTH_SHORT).show()
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) { }
            })
            .dispatch()
    }
    private fun saveImageUrlToDatabase(url: String) {
        val userID = auth.currentUser?.uid
        database = FirebaseDatabase.getInstance().getReference().child("Users").child(userID!!)
        database.child("Profile ImageUrl").setValue(url).addOnSuccessListener {
            Toast.makeText(this, "Profile photo updated", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        loadProfileImage()
    }
    private fun loadProfileImage() {
        val userID = auth.currentUser?.uid
        database = FirebaseDatabase.getInstance().getReference().child("Users").child(userID!!)
        database.child("Profile ImageUrl").get().addOnSuccessListener { snapshot ->
            val imageUrl = snapshot.value?.toString()
            if (!imageUrl.isNullOrEmpty()) {
                Picasso.get().load(imageUrl).into(profileImageVIew)
            }
        }
    }
    private fun updateDetails() {
        val user = auth.currentUser ?: return

        val userId = user.uid
        val userEmail = user.email.orEmpty()

        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)

        userRef.get().addOnSuccessListener { snapshot ->

            val userName = snapshot.child("name").getValue(String::class.java).orEmpty()

            binding.nameEditText.setText(userName)
            binding.textView18.text = userName

            val phoneNo = snapshot
                .child("Address")
                .child("phone no")
                .getValue(String::class.java)
                .orEmpty()

            binding.phoneEditText.setText(phoneNo)
        }.addOnFailureListener { e ->
                Log.e("Firebase", "Failed to load user data", e)
        }

        binding.emailEditText.setText(userEmail)
    }
    private fun updateProfile() {
        val user = auth.currentUser ?: return

        val userId = user.uid
        val name = binding.nameEditText.text.toString().trim()
        val phoneNo = binding.phoneEditText.text.toString().trim()

        if (name.isEmpty() || phoneNo.isEmpty()) {
            Toast.makeText(this,"Name or Phone is empty", Toast.LENGTH_SHORT).show()
            Log.e("Profile", "Name or Phone is empty")
            return
        }

        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)

        val updates = hashMapOf<String, Any>(
            "name" to name,
            "phone no" to phoneNo
        )

        userRef.updateChildren(updates).addOnSuccessListener {
            Toast.makeText(this,"Profile updated successfully", Toast.LENGTH_SHORT).show()
            Log.d("Profile", "Profile updated successfully")
        }.addOnFailureListener { e ->
            Toast.makeText(this,"Failed to update profile", Toast.LENGTH_SHORT).show()
            Log.e("Profile", "Failed to update profile", e)
        }
    }
    private fun Int.dpToPx(view: View): Int =
        (this * view.resources.displayMetrics.density).toInt()
    private fun handleBottomNavPosition() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->

            val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

            // Typical values:
            // Gesture: 16–24dp
            // 3-button: 48–80dp

            val threshold = 40.dpToPx(binding.root)

            binding.main.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = if (navBarHeight > threshold) {
                    navBarHeight   // 3-button → move up
                } else {
                    0              // Gesture → stay at bottom
                }
            }

            insets
        }
    }
    private fun setStatusBarIconsTheme(activity: Activity) {
        val window = activity.window
        val decorView = window.decorView
        val insetsController = WindowInsetsControllerCompat(window, decorView)

        // Detect current theme
        val isDarkTheme =
            (activity.resources.configuration.uiMode
                    and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

        // Set icon color automatically
        if (isDarkTheme) {
            // Light icons for dark theme
            insetsController.isAppearanceLightStatusBars = true
        } else {
            // Dark icons for light theme
            insetsController.isAppearanceLightStatusBars = true
        }
    }
}