package com.example.imagegalleryapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit

class GalleryActivity : AppCompatActivity() {
    private val imageMap = mapOf(
        R.id.img1 to R.drawable.img1,
        R.id.img2 to R.drawable.img2,
        R.id.img3 to R.drawable.img3,
        R.id.img4 to R.drawable.img4,
        R.id.img5 to R.drawable.img5,
        R.id.img6 to R.drawable.img6
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        //Handle image clicks
        imageMap.forEach { (id, res) ->
            findViewById<ImageView>(id).setOnClickListener {
                val intent = Intent(this, ImageDescriptionActivity::class.java)
                intent.putExtra("imageId", res)
                startActivity(intent)
            }
        }

        //Handle logout
        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            val sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)

            //Saved login info (username, password, rememberMe)
            sharedPreferences.edit {
                putBoolean("isLoggedIn", false)
            }

            //Go back to login screen
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
