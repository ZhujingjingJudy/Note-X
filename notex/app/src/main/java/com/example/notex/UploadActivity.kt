package com.example.notex

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultCallback

class UploadActivity : AppCompatActivity() {

    private lateinit var buttonUploadFile: Button
    private lateinit var textViewFileName: TextView
    private lateinit var uploadFileLauncher: ActivityResultLauncher<Intent>
    private lateinit var switchVocal: Switch
    private lateinit var switchInstrument: Switch
    private lateinit var buttonGetStarted: Button
    private lateinit var buttonBackToHome: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        buttonUploadFile = findViewById(R.id.buttonUploadFile)
        textViewFileName = findViewById(R.id.textViewFileName)
        switchVocal = findViewById(R.id.switchVocal)
        switchInstrument = findViewById(R.id.switchInstrument)
        buttonGetStarted = findViewById(R.id.buttonGetStarted)
        buttonBackToHome = findViewById(R.id.buttonBackToHome)

        // Register activity result launcher for file upload
        uploadFileLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.data?.let { uri ->
                        val fileName = getFileName(uri)
                        textViewFileName.text = fileName
                    }
                }
            }
        )

        // Set onClickListener for the upload button
        buttonUploadFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            uploadFileLauncher.launch(intent)
        }

        // Set onClickListener for the back to home button
        buttonBackToHome.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }

        // Add your get started functionality here if needed
        buttonGetStarted.setOnClickListener{
            val intent = Intent(this, TransActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getFileName(uri: Uri): String? {
        var fileName: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                fileName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
        return fileName
    }
}
