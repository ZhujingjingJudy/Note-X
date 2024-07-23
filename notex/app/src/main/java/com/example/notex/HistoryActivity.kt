package com.example.notex

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: HistoryAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var buttonBackToHome: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        viewManager = LinearLayoutManager(this)
        viewAdapter = HistoryAdapter(getHistoryData())  // Replace with your data source

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        buttonBackToHome = findViewById(R.id.buttonBackToHome)

        buttonBackToHome.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getHistoryData(): List<HistoryItem> {
        // Replace with actual data fetching logic
        return listOf(
            HistoryItem("Audio 1", "audio1.mp3"),
            HistoryItem("Audio 2", "audio2.mp3")
        )
    }
}

data class HistoryItem(val title: String, val fileName: String)
