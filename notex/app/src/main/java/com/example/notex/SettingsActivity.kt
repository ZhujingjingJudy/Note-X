package com.example.notex

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var changeProfilePictureButton: Button
    private lateinit var currentNameTextView: TextView
    private lateinit var changeNameButton: Button
    private lateinit var logoutButton: Button
    private lateinit var buttonBackToHome: Button

    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        profileImageView = findViewById(R.id.profileImageView)
        changeProfilePictureButton = findViewById(R.id.changeProfilePictureButton)
        currentNameTextView = findViewById(R.id.currentNameTextView)
        changeNameButton = findViewById(R.id.changeNameButton)
        logoutButton = findViewById(R.id.logoutButton)

        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val username = sharedPref.getString("username", "Default")
        
        if (username != null) {
            Log.d("UserNameSetted", username)
            currentNameTextView.text = "ID: $username"
        }else{
            Log.d("NoUserName", "noName")
        }

        changeProfilePictureButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        changeNameButton.setOnClickListener {
            showChangeNameDialog()
        }

        logoutButton.setOnClickListener {
            performLogout()
        }

        buttonBackToHome = findViewById(R.id.buttonBackToHome)

        buttonBackToHome.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showChangeNameDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Change Name")

        val input = EditText(this)
        input.hint = "Enter new name"
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val newName = input.text.toString()
            if (newName.isNotEmpty()) {
                currentNameTextView.text = "ID: $newName"
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun performLogout() {
        // Clear any user data and navigate to the login activity
        // You may want to clear shared preferences or any stored session data here
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImage: Uri? = data.data
            profileImageView.setImageURI(selectedImage)
        }
    }
}
