package com.example.nike

import android.app.Activity
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import com.example.nike.databinding.ActivityCheckOutBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random

class CheckOut : AppCompatActivity() {
    private val CHANNEL_ID = "nike"
    private val CHANNEL_NAME = "orders_notification"
    private lateinit var binding: ActivityCheckOutBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var isExpanded = false
    private lateinit var addressEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var countryEditText: EditText
    private lateinit var postCodeEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var paymentSuccessDialog: Dialog
    private lateinit var transactionIDText: TextView
    private lateinit var orderIDText: TextView
    private lateinit var currentDateText: TextView
    private lateinit var currentTimeText: TextView
    private lateinit var amountText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCheckOutBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        enableEdgeToEdgeWithInsets(binding.root,binding.main)
        setStatusBarIconsTheme(this)

        binding.backArrowImage.setOnClickListener {
            val intent = Intent(this, MyCart::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }
        binding.backArrowbtn.setOnClickListener {
            val intent = Intent(this, MyCart::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }

        auth = FirebaseAuth.getInstance()
        val userID = auth.currentUser?.uid
        val userEmail = auth.currentUser?.email

        val subTotal = intent.getDoubleExtra("subTotal", 0.0)
        val shoppingFee = intent.getDoubleExtra("shoppingFee", 0.0)
        val totalCost = intent.getDoubleExtra("totalCost", 0.0)

        binding.subTotalText.text = String.format(Locale.US, "$ %.2f", subTotal)
        binding.shoppingFeeText.text = String.format(Locale.US, "$ %.2f", shoppingFee)
        binding.totalCostText.text = String.format(Locale.US, "$ %.2f", totalCost)

        updateCreditCardDetail(userID)
 
        bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.credit_card_layout,null)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)
        bottomSheetDialog.setOnShowListener {
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 800
        }
        view.scaleY = 0.8f
        view.animate()
            .scaleY(1f)
            .setInterpolator(OvershootInterpolator())
            .setDuration(400)
            .start()

        val editText1 = bottomSheetDialog.findViewById<TextInputEditText>(R.id.editText1)
        val editText2 = bottomSheetDialog.findViewById<TextInputEditText>(R.id.editText2)
        val editText3 = bottomSheetDialog.findViewById<TextInputEditText>(R.id.editText3)
        val editText4 = bottomSheetDialog.findViewById<TextInputEditText>(R.id.editText4)
        val textView1 = bottomSheetDialog.findViewById<TextView>(R.id.textView2)
        val textView2 = bottomSheetDialog.findViewById<TextView>(R.id.textView4)
        val textView3 = bottomSheetDialog.findViewById<TextView>(R.id.textView6)
        val textView4 = bottomSheetDialog.findViewById<TextView>(R.id.textView8)

        binding.addCardBtn.setOnClickListener {
            editText1?.setSelection(editText1.text!!.length)
            editText2?.setSelection(editText2.text!!.length)
            editText3?.setSelection(editText3.text!!.length)
            editText4?.setSelection(editText4.text!!.length)

            database = FirebaseDatabase.getInstance().getReference().child("Users").child(userID!!)
                .child("Credit Card")
            database.get().addOnSuccessListener { it ->
                if (it.exists()) {
                    val cardHolderName = it.child("cardHolderName").value.toString()
                    val cardNumber = it.child("card Number").value.toString()
                    val expiryDate = it.child("expiry Date").value.toString()
                    val cvv = it.child("CVV").value.toString()
                    val maskedCard = maskCardNumber(cardNumber)

                    editText1?.setText(cardHolderName)
                    editText2?.setText(cardNumber)
                    editText3?.setText(expiryDate)
                    editText4?.setText(cvv)

                    textView1?.text = maskedCard
                    textView2?.text = cardHolderName
                    textView3?.text = expiryDate
                    textView4?.text = cvv
                } else {
                    "Card Number".also { textView1?.text = it }
                    "Card Holder Name".also { textView2?.text = it }
                    "00/00".also { textView3?.text = it }
                    "000".also { textView4?.text = it }

                    editText1?.setText("Card Holder Name")
                    editText2?.setText("Card Number")
                    editText3?.setText("00/00")
                    editText4?.setText("000")
                }
            }.addOnFailureListener {
                Log.e("FireBaseDB", "Failed to fetch Credit Card Details")
            }
            bottomSheetDialog.show()
        }

        val continueBtn = bottomSheetDialog.findViewById<MaterialButton>(R.id.continueBtn)
        continueBtn?.setOnClickListener {
            val cardHolderName = editText1?.text.toString().trim()
            val cardNumber = editText2?.text.toString().trim()
            val expiryDate = editText3?.text.toString().trim()
            val cvv = editText4?.text.toString().trim()

            val cardDetails = mapOf(
                "cardHolderName" to cardHolderName,
                "card Number" to cardNumber,
                "expiry Date" to expiryDate,
                "CVV" to cvv
            )
            database = FirebaseDatabase.getInstance().getReference().child("Users").child(userID!!)
                .child("Credit Card")
            database.setValue(cardDetails).addOnSuccessListener {
                Log.d("Credit Card", "Credit Card Saved : $cardDetails")
            }.addOnFailureListener {
                Log.e("Credit Card", "Failed to save Credit Card")
            }
            updateCreditCardDetail(userID)
            bottomSheetDialog.dismiss()
        }

        paymentSuccessDialog = Dialog(this)
        paymentSuccessDialog.setContentView(R.layout.payment_successful)
        paymentSuccessDialog.window?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.bg_dialog_box
            )
        )
        transactionIDText = paymentSuccessDialog.findViewById(R.id.textView9)
        orderIDText = paymentSuccessDialog.findViewById(R.id.textView10)
        currentDateText = paymentSuccessDialog.findViewById(R.id.textView11)
        currentTimeText = paymentSuccessDialog.findViewById(R.id.textView12)
        amountText = paymentSuccessDialog.findViewById(R.id.textView14)
        amountText.text = String.format(Locale.US, "$ %.2f", totalCost)
        val amountText2 = paymentSuccessDialog.findViewById<TextView>(R.id.textView17)
        amountText2.text = String.format(Locale.US, "Successfully Paid $ %.2f", totalCost)

        val backBtn = paymentSuccessDialog.findViewById<MaterialButton>(R.id.backBtn)
        backBtn.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
            paymentSuccessDialog.dismiss()
        }

        createNotificationChannel()

        binding.paymentBtn.setOnClickListener {
            transactionDetails(userID)
            paymentSuccessDialog.show()
            showNotification()
        }

        addressEditText = binding.address.editField
        cityEditText = binding.city.editField
        countryEditText = binding.country.editField
        postCodeEditText = binding.postcode.editField
        phoneEditText = binding.phone.editField

        database = FirebaseDatabase.getInstance().getReference().child("Users").child(userID!!)
            .child("Address")
        database.get().addOnSuccessListener {
            if (it.exists()) {
                val address = it.child("address line").value.toString()
                val city = it.child("city").value.toString()
                val country = it.child("country").value.toString()
                val postCode = it.child("postcode").value.toString()
                val phone = it.child("phone no").value.toString()

                setupEditable(findViewById(R.id.phone), phone, InputType.TYPE_CLASS_PHONE)
                setupEditable(findViewById(R.id.address), address, InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS)
                setupEditable(findViewById(R.id.city), city, InputType.TYPE_CLASS_TEXT)
                setupEditable(findViewById(R.id.country), country, InputType.TYPE_CLASS_TEXT)
                setupEditable(findViewById(R.id.postcode), postCode, InputType.TYPE_CLASS_NUMBER)
                updateFullAddress()
                Log.d("FireBaseDB", "$address, $city, $postCode - $country")
            } else {
                setupEditable(findViewById(R.id.phone), "00000 00000", InputType.TYPE_CLASS_PHONE)
                setupEditable(findViewById(R.id.address), "address", InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS)
                setupEditable(findViewById(R.id.city), "city", InputType.TYPE_CLASS_TEXT)
                setupEditable(findViewById(R.id.country), "country", InputType.TYPE_CLASS_TEXT)
                setupEditable(findViewById(R.id.postcode), "postCode", InputType.TYPE_CLASS_NUMBER)
                updateFullAddress()
            }
        }.addOnFailureListener {
            Log.e("FireBaseDB", "Failed to fetch Address")
        }

        setupEditable(findViewById(R.id.email), "$userEmail",InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)

        val arrowDownIcon = binding.arrowDown
        val expandedLayout = binding.expandableLayout
        val expandedAddressLayout = binding.expandableLayout1
        arrowDownIcon.setOnClickListener {
            isExpanded = !isExpanded
            if (isExpanded) {
                expandedLayout.animate()
                    .alpha(0f)
                    .translationY(-20f)
                    .setDuration(250)
                    .withEndAction { expandedLayout.visibility = View.GONE }
                    .start()
                expandedAddressLayout.visibility = View.VISIBLE
                expandedAddressLayout.alpha = 0f
                expandedAddressLayout.translationY = -20f
                expandedAddressLayout.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(250)
                    .start()
                arrowDownIcon.setImageResource(R.drawable.arrow_up)
            } else {
                expandedLayout.visibility = View.VISIBLE
                expandedLayout.alpha = 0f
                expandedLayout.translationY = -20f
                expandedLayout.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(250)
                    .start()
                expandedAddressLayout.animate()
                    .alpha(0f)
                    .translationY(-20f)
                    .setDuration(250)
                    .withEndAction { expandedAddressLayout.visibility = View.GONE }
                    .start()
                arrowDownIcon.setImageResource(R.drawable.arrow_down)
            }
        }
    }
    private fun setupEditable(row: View, value: String, inputType: Int) {
        val editField = row.findViewById<EditText>(R.id.editField)
        val btnEdit = row.findViewById<ImageView>(R.id.editBtn)

        editField.setText(value)
        editField.inputType = inputType
        editField.isFocusable = false
        editField.isFocusableInTouchMode = false
        editField.isCursorVisible = false
        val keyboard = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        btnEdit.setOnClickListener {
            if (!editField.isFocusable) {
                editField.isFocusable = true
                editField.isEnabled = true
                editField.isFocusableInTouchMode = true
                editField.isCursorVisible = true

                if (editField.text.isNullOrEmpty()) {
                    editField.setSelection(0)
                } else {
                    editField.setSelection(editField.text.length)
                }
                editField.requestFocus()

                keyboard.showSoftInput(editField, InputMethodManager.SHOW_IMPLICIT)
            } else {
                editField.isFocusable = false
                editField.isFocusableInTouchMode = false
                editField.isCursorVisible = false

                keyboard.hideSoftInputFromWindow(editField.windowToken, 0)

                val updatedValue = editField.text.toString()
                updateFullAddress()
                Toast.makeText(this, "Updated: $updatedValue", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun updateFullAddress() {
        val address = addressEditText.text.toString().trim()
        val city = cityEditText.text.toString().trim()
        val country = countryEditText.text.toString().trim()
        val postcode = postCodeEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()

        val fullAddress = "$address, $city, $postcode - $country"
        binding.textView8.text = fullAddress

        auth = FirebaseAuth.getInstance()
        val userID = auth.currentUser?.uid
        database = FirebaseDatabase.getInstance().getReference().child("Users").child(userID!!)
            .child("Address")
        val userAddress = mapOf(
            "address line" to address,
            "city" to city,
            "country" to country,
            "postcode" to postcode,
            "phone no" to phone
        )
        database.setValue(userAddress).addOnSuccessListener {
            Log.d("Address", "Address Saved : $fullAddress")
        }.addOnFailureListener {
            Log.e("Address", "Failed to save address")
        }
    }
    private fun updateCreditCardDetail(userID: String?) {
        database = FirebaseDatabase.getInstance().getReference().child("Users").child(userID!!)
            .child("Credit Card")
        database.get().addOnSuccessListener {
            if (it.exists()) {
                val cardHolderName = it.child("cardHolderName").value.toString()
                val cardNumber = it.child("card Number").value.toString()
                val maskedCard = maskCardNumber(cardNumber)
                binding.textView10.text = cardHolderName
                binding.textView11.text = maskedCard
            }
        }
    }
    private fun transactionDetails(userID: String?) {
        val transactionID = (1..12)
            .map { Random.nextInt(0, 10) }
            .joinToString("")
            .chunked(4)
            .joinToString(" ")

        val orderID = (1..8)
            .map { Random.nextInt(0,10) }
            .joinToString("")

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(java.util.Date())

        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currentTime = timeFormat.format(java.util.Date())

        transactionIDText.text = transactionID
        orderIDText.text = orderID
        currentDateText.text = currentDate
        currentTimeText.text = currentTime
        val total = amountText.text

        val address = addressEditText.text.toString().trim()
        val city = cityEditText.text.toString().trim()
        val country = countryEditText.text.toString().trim()
        val postcode = postCodeEditText.text.toString().trim()

        val fullAddress = "$address, $city, $postcode - $country"

        val details = mapOf(
            "transactionID" to transactionID,
            "orderID" to orderID,
            "date" to currentDate,
            "time" to currentTime,
            "amount" to total,
            "address" to fullAddress
        )
        database = FirebaseDatabase.getInstance().getReference().child("Users").child(userID!!)
            .child("Transactions Details")
        database.child(transactionID).setValue(details).addOnSuccessListener {
            Log.d("Transaction Details", "Transaction Detail Saved")
        }.addOnFailureListener {
            Log.e("Transaction Details", "Transaction Detail Failed to Saved")
        }

        val cartList = ArrayList<CartItem>()
        database = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("MyCart")
        database.get().addOnSuccessListener { snapshot ->
            for (item in snapshot.children) {
                val shoe = item.getValue(CartItem::class.java)
                shoe?.let {
                    cartList.add(it)
                }
            }

            val transactionRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID)
                .child("Transactions Details").child(transactionID)
            for (shoe in cartList) {
                val item = mapOf(
                    "shoeImage" to shoe.imageURL,
                    "shoeID" to shoe.id,
                    "shoeName" to shoe.name,
                    "price" to shoe.price,
                    "size" to shoe.shoeSize,
                    "quantity" to shoe.quantity
                )

                transactionRef.child("products").child(shoe.id.toString()).setValue(item)
                    .addOnSuccessListener {
                        Log.d("Product Detail", "product saved")
                    }
                    .addOnFailureListener {
                        Log.e("Product Detail", "failed to save product")
                    }
            }
        }
    }
    private fun maskCardNumber(cardNumber: String): String {
        val digitsOnly = cardNumber.replace(" ","")
        val masked = digitsOnly.mapIndexed { index, ch ->
            if (index < 8) ch else '*'
        }.joinToString("")

        return masked.chunked(4).joinToString(" ")
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun showNotification() {
        val notification = NotificationCompat.Builder(this,CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Order Confirmed 👟")
            .setContentText("Your order has been placed successfully.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(this@CheckOut,android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            notify(1001,notification.build())
        }
    }
    private fun enableEdgeToEdgeWithInsets(rootView: View, LayoutView: View) {
        val activity = rootView.context as ComponentActivity
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            LayoutView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = systemBars.bottom
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