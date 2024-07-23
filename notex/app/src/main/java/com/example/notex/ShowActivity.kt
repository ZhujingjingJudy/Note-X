package com.example.notex

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.example.notex.R
import java.io.File

class ShowActivity : AppCompatActivity() {

    // on below line we are creating variable for image view.
    private lateinit var buttonBackToHome: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        lateinit var imageIV: ImageView
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sheet)
        buttonBackToHome = findViewById(R.id.buttonBackToHome)

        buttonBackToHome.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
        }

        // on below line we are creating an image file and
        // specifying path for the image file on below line.
        val imgFile = File("/drawable/image.png")

        // on below line we are initializing variables with ids.
        imageIV = findViewById(R.id.idIVImage)

        // on below line we are checking if the image file exist or not.
        if (imgFile.exists()) {

            // on below line we are creating an image bitmap variable
            // and adding a bitmap to it from image file.
            val imgBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)

            // on below line we are setting bitmap to our image view.
            imageIV.setImageBitmap(imgBitmap)
        }
    }
}