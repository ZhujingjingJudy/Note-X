package com.example.notex

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.example.notex.plant.ChartActivity

class HomePageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        val buttonUpload: ImageButton = findViewById(R.id.buttonUpload)
        val buttonHistory: ImageButton = findViewById(R.id.buttonHistory)
        val buttonSearch: ImageButton = findViewById(R.id.buttonSearch)
        val buttonSettings: ImageButton = findViewById(R.id.buttonSettings)
        val btnEdit: Button = findViewById(R.id.button)

        buttonUpload.setOnClickListener {
            // Navigate to UploadActivity
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }

        buttonHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        buttonSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        buttonSettings.setOnClickListener {
            val username = intent.getStringExtra("username")
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }
        btnEdit.setOnClickListener {
            val intent = Intent(this, ChartActivity::class.java)
            startActivity(intent)
        }
    }
}
