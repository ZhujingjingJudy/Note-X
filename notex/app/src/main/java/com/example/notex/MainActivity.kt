package com.example.notex

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.notex.UserInfoStore.postLoginInfo

class MainActivity : AppCompatActivity() {

    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonSignIn: Button
    private lateinit var buttonSignUp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonSignIn = findViewById(R.id.buttonSignIn)
        buttonSignUp = findViewById(R.id.buttonSignUp)

        buttonSignIn.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()
            val userinfo = UserInfo(username, password)
            postLoginInfo(applicationContext, userinfo,
                onSuccess = {
                val intent = Intent(this, HomePageActivity::class.java)
                intent.putExtra("username", username)
                startActivity(intent)
            }, onFail = {
                runOnUiThread {
                    Toast.makeText(this, "Wrong Username or Password!", Toast.LENGTH_SHORT).show()
                }
            })
        }

        buttonSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}
