package com.example.notex

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.notex.UserInfoStore.postLoginInfo
import android.Manifest


class MainActivity : AppCompatActivity() {

    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonSignIn: Button
    private lateinit var buttonSignUp: Button
    private val REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissions()

    }

    private fun requestPermissions() {
        // 检查存储权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 如果权限未被授予，则请求权限
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE)
        } else {
            // 权限已被授予，可以执行相关操作
            performActionWithPermission()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予，可以执行相关操作
                performActionWithPermission()
            } else {
                // 权限被拒绝，提示用户
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 执行需要权限的操作
    private fun performActionWithPermission() {
        // 在这里放置需要存储权限的代码
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
